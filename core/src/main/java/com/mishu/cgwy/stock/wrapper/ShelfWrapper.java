package com.mishu.cgwy.stock.wrapper;

import com.mishu.cgwy.stock.domain.Shelf;
import lombok.Data;

@Data
public class ShelfWrapper {
    private Long id;
    private String name;
    private DepotWrapper depot;
    private String area;
    private String row;
    private String number;
    private String shelfCode;

    public ShelfWrapper() {
    }

    public ShelfWrapper(Shelf shelf) {
        this.id = shelf.getId();
        this.name = shelf.getName();
        this.depot = new DepotWrapper(shelf.getDepot());
        this.area = shelf.getArea();
        this.row = shelf.getRow();
        this.number = shelf.getNumber();
        this.shelfCode = shelf.getShelfCode();
    }
}
