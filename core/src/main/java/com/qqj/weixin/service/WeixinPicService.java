package com.qqj.weixin.service;

import com.qqj.weixin.domain.WeixinPic;
import com.qqj.weixin.domain.WeixinPic_;
import com.qqj.weixin.domain.WeixinUser_;
import com.qqj.weixin.repository.WeixinPicRepository;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
public class WeixinPicService {
    @Autowired
    private WeixinPicRepository weixinPicRepository;

    public WeixinPic findWeixinPicByWeixinUserIdAndType(final Long id, final Short type) {
        List<WeixinPic> list = weixinPicRepository.findAll(new Specification<WeixinPic>() {
            @Override
            public Predicate toPredicate(Root<WeixinPic> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get(WeixinPic_.user).get(WeixinUser_.id), id));
                predicates.add(cb.equal(root.get(WeixinPic_.type), type));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });

        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    public void save(WeixinPic weixinPic) {
        weixinPicRepository.save(weixinPic);
    }
}
