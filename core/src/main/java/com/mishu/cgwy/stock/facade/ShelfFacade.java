package com.mishu.cgwy.stock.facade;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.error.UserDefinedException;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.stock.domain.Depot;
import com.mishu.cgwy.stock.domain.Shelf;
import com.mishu.cgwy.stock.domain.Stock;
import com.mishu.cgwy.stock.dto.ShelfData;
import com.mishu.cgwy.stock.dto.ShelfRequest;
import com.mishu.cgwy.stock.service.DepotService;
import com.mishu.cgwy.stock.service.ShelfService;
import com.mishu.cgwy.stock.service.StockService;
import com.mishu.cgwy.stock.wrapper.ShelfWrapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ShelfFacade {

    @Autowired
    private DepotService depotService;
    @Autowired
    private ShelfService shelfService;
    @Autowired
    private StockService stockService;

    @Transactional(readOnly = true)
    public QueryResponse<ShelfWrapper> getShelfList(ShelfRequest request, AdminUser adminUser) {

        QueryResponse<ShelfWrapper> res = new QueryResponse<ShelfWrapper>();
        Page<Shelf> page = shelfService.getShelfList(request, adminUser);
        for (Shelf shelf : page.getContent()) {
            res.getContent().add(new ShelfWrapper(shelf));
        }
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(page.getTotalElements());
        return res;
    }

    @Transactional
    public ShelfWrapper addShelf(ShelfData shelfData){
        Shelf shelf = new Shelf();
        shelf.setArea(shelfData.getArea());
        shelf.setDepot(depotService.findOne(shelfData.getDepotId()));
        shelf.setName(shelfData.getName());
        shelf.setNumber(shelfData.getNumber());
        shelf.setRow(shelfData.getRow());
        shelf.setShelfCode(shelfData.getShelfCode());
        return new ShelfWrapper(shelfService.addShelf(shelf));
    }

    @Transactional
    public ShelfWrapper updateShelf(Long id, ShelfData shelfData){
        Shelf shelf = shelfService.findOne(id);
        Shelf findShelf = shelfService.findShelfByDepotAndShelfCode(shelfData.getDepotId(), shelfData.getShelfCode());
        if (findShelf != null && !findShelf.getId().equals(shelf.getId())){
            throw new UserDefinedException("该仓库下存在货位 " + findShelf.getShelfCode());
        }

        shelf.setArea(shelfData.getArea());
        shelf.setDepot(depotService.findOne(shelfData.getDepotId()));
        shelf.setName(shelfData.getName());
        shelf.setNumber(shelfData.getNumber());
        shelf.setRow(shelfData.getRow());
        shelf.setShelfCode(shelfData.getShelfCode());
        return new ShelfWrapper(shelfService.saveShelf(shelf));
    }

    @Transactional
    public void deleteShelf(ShelfData shelfData) {
        Set<Long> ids = shelfData.getShelfIds();
        Iterator<Long> idIterator = ids.iterator();
        while (idIterator.hasNext()) {
            Long id = idIterator.next();
            List<Stock> stocks = stockService.findSockByShelf(id);
            if (stocks.isEmpty()) {
                shelfService.deleteShelf(id);
            } else {
                Shelf shelf = shelfService.findOne(id);
                throw new UserDefinedException("货位 " + shelf.getShelfCode() + " 已使用, 不能删除");
            }
        }
    }

    @Transactional(readOnly = true)
    public ShelfWrapper findShelf(Long id) {
        return new ShelfWrapper(shelfService.findOne(id));
    }

    @Transactional
    public void addBatchShelf(ShelfData shelfData){
        if (StringUtils.isNotBlank(shelfData.getArea()) && shelfData.getAreaNum() != null) {
            List<Shelf> shelfs = new ArrayList<>();
            Depot depot = depotService.findOne(shelfData.getDepotId());

            Integer areaNum = shelfData.getAreaNum();
            if (areaNum <= 0) {
                areaNum = 0;
            } else if (areaNum > 99) {
                areaNum = 99;
            }

            int area = Integer.parseInt(shelfData.getArea());
            while (areaNum > 0) {

                if (area > 99) {
                    break;
                }
                if (StringUtils.isNotBlank(shelfData.getRow()) && shelfData.getRowNum() != null) {
                    Integer rowNum = shelfData.getRowNum();
                    if (rowNum <= 0) {
                        rowNum = 0;
                    } else if (rowNum > 99) {
                        rowNum = 99;
                    }

                    int row = Integer.parseInt(shelfData.getRow());
                    while (rowNum > 0) {

                        if (row > 99) {
                            break;
                        }

                        if (StringUtils.isNotBlank(shelfData.getNumber()) && shelfData.getNumberNum() != null) {
                            Integer numberNum = shelfData.getNumberNum();
                            if (numberNum <= 0) {
                                numberNum = 0;
                            } else if (numberNum > 99) {
                                numberNum = 99;
                            }

                            int number = Integer.parseInt(shelfData.getNumber());
                            while (numberNum > 0) {

                                if (number > 99) {
                                    break;
                                }

                                Shelf shelf = new Shelf();
                                shelf.setArea(String.format("%02d",area));
                                shelf.setDepot(depot);
                                shelf.setName(generalCodeName(area, row, number)[1]);
                                shelf.setNumber(String.format("%02d", number));
                                shelf.setRow(String.format("%02d", row));
                                shelf.setShelfCode(generalCodeName(area, row, number)[0]);
                                shelfs.add(shelf);

                                number ++;
                                numberNum --;
                            }
                        } else {
                            Shelf shelf = new Shelf();
                            shelf.setArea(String.format("%02d",area));
                            shelf.setDepot(depot);
                            shelf.setName(generalCodeName(area, row, null)[1]);
                            shelf.setNumber(null);
                            shelf.setRow(String.format("%02d", row));
                            shelf.setShelfCode(generalCodeName(area, row, null)[0]);
                            shelfs.add(shelf);
                        }

                        row ++;
                        rowNum --;
                    }
                } else {
                    Shelf shelf = new Shelf();
                    shelf.setArea(String.format("%02d",area));
                    shelf.setDepot(depot);
                    shelf.setName(generalCodeName(area, null, null)[1]);
                    shelf.setNumber(null);
                    shelf.setRow(null);
                    shelf.setShelfCode(generalCodeName(area, null, null)[0]);
                    shelfs.add(shelf);
                }

                area ++;
                areaNum --;
            }

            if (!shelfs.isEmpty()) {
                List<String> shelfCodes = new ArrayList<>(Collections2.transform(shelfs, new Function<Shelf, String>() {
                    @Override
                    public String apply(Shelf input) {
                        return input.getShelfCode();
                    }
                }));
                List<Shelf> findShelfs = shelfService.findShelfByDepotAndShelfCodes(depot.getId(), shelfCodes);
                Map<String,Shelf> mappedShelfs = Maps.uniqueIndex(findShelfs, new Function<Shelf, String>() {
                    public String apply(Shelf input) {
                        return input.getShelfCode();
                    }
                });
                for (Shelf addShelf : shelfs) {
                    if (!mappedShelfs.containsKey(addShelf.getShelfCode())) {
                        shelfService.saveShelf(addShelf);
                    }
                }
            }
        }
    }

    private String[] generalCodeName (Integer area, Integer row, Integer number) {
        String[] codeNames = new String[] {null, null};
        String code = "";
        String name = "";
        if (area != null) {
            code += String.format("%02d",area);
            name += String.format("%02d",area) + "区";
        }
        if (row != null) {
            code += String.format("%02d",row);
            name += String.format("%02d",row) + "排";
        }
        if (number != null) {
            code += String.format("%02d",number);
            name += String.format("%02d",number) + "号";
        }
        codeNames[0] = code;
        codeNames[1] = name;
        return codeNames;
    }

    @Transactional(readOnly = true)
    public ShelfWrapper findShelfByDepotAndShelfCode(ShelfData shelfData){
        Shelf findShelf = shelfService.findShelfByDepotAndShelfCode(shelfData.getDepotId(), shelfData.getShelfCode());
        if (findShelf != null){
            ShelfWrapper shelfWrapper = new ShelfWrapper();
            shelfWrapper.setName(findShelf.getName());
            shelfWrapper.setId(findShelf.getId());
            return shelfWrapper;
        } else {
            return null;
        }
    }
}
