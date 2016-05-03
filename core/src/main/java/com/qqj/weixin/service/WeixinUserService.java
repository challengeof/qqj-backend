package com.qqj.weixin.service;

import com.qqj.response.query.QueryResponse;
import com.qqj.response.query.WeixinUserStatisticsResponse;
import com.qqj.utils.EntityUtils;
import com.qqj.weixin.controller.WeixinUserListRequest;
import com.qqj.weixin.domain.WeixinUser;
import com.qqj.weixin.domain.WeixinUser_;
import com.qqj.weixin.enumeration.WeixinUserGroup;
import com.qqj.weixin.enumeration.WeixinUserStatus;
import com.qqj.weixin.repository.WeixinUserRepository;
import com.qqj.weixin.wrapper.WeixinUserWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class WeixinUserService {

    private static Logger logger = LoggerFactory.getLogger(WeixinUserService.class);

    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private WeixinUserRepository weixinUserRepository;

    @Transactional(readOnly = true)
    public QueryResponse<WeixinUserWrapper> getWeixinUserList(final WeixinUserListRequest request) {
        final PageRequest pageRequest = new PageRequest(request.getPage(), request.getPageSize());

        Page<WeixinUser> page = weixinUserRepository.findAll(new Specification<WeixinUser>() {
            @Override
            public Predicate toPredicate(Root<WeixinUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();


                if (request.getGroup() != null) {
                    WeixinUserGroup weixinUserGroup = WeixinUserGroup.get(request.getGroup());
                    if (weixinUserGroup.getStart() != null) {
                        try {
                            predicates.add(cb.greaterThanOrEqualTo(root.get(WeixinUser_.birthday), df.parse(weixinUserGroup.getStart())));
                        } catch (ParseException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }

                    if (weixinUserGroup.getEnd() != null) {
                        try {
                            predicates.add(cb.lessThan(root.get(WeixinUser_.birthday), df.parse(weixinUserGroup.getEnd())));
                        } catch (ParseException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }

                if (request.getStatus() != null) {
                    predicates.add(cb.equal(root.get(WeixinUser_.status), request.getStatus()));
                }

                predicates.add(cb.notEqual(root.get(WeixinUser_.status), WeixinUserStatus.STATUS_TMP));

                if (request.getTelephone() != null) {
                    predicates.add(cb.like(root.get(WeixinUser_.telephone), String.format("%%%s%%", request.getTelephone())));
                }

                query.orderBy(cb.asc(root.get(WeixinUser_.id)));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageRequest);

        QueryResponse<WeixinUserWrapper> response = new QueryResponse<>();

        response.setContent(EntityUtils.toWrappers(page.getContent(), WeixinUserWrapper.class));
        response.setPage(request.getPage());
        response.setPageSize(request.getPageSize());
        response.setTotal(page.getTotalElements());

        return response;
    }

    public void auditWeixinUser(Long id, Short status) {
        WeixinUser weixinUser = weixinUserRepository.findOne(id);
        weixinUser.setStatus(status);
        weixinUser.setAuditTime(new Date());
        weixinUserRepository.save(weixinUser);
    }

    public WeixinUserStatisticsResponse weixinUserStatistics() {
        WeixinUserStatisticsResponse res = new WeixinUserStatisticsResponse();
        long group1Sum1 = weixinUserRepository.count(new Specification<WeixinUser>() {
            @Override
            public Predicate toPredicate(Root<WeixinUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                try {
                    predicates.add(cb.lessThan(root.get(WeixinUser_.birthday), df.parse(WeixinUserGroup.Group_1.getEnd())));
                } catch (ParseException e) {
                    logger.error(e.getMessage(), e);
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
        long group1Sum2 = weixinUserRepository.count(new Specification<WeixinUser>() {
            @Override
            public Predicate toPredicate(Root<WeixinUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                try {
                    predicates.add(cb.lessThan(root.get(WeixinUser_.birthday), df.parse(WeixinUserGroup.Group_1.getEnd())));
                } catch (ParseException e) {
                    logger.error(e.getMessage(), e);
                }
                predicates.add(cb.equal(root.get(WeixinUser_.status), WeixinUserStatus.STATUS_0.getValue()));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
        long group1Sum3 = weixinUserRepository.count(new Specification<WeixinUser>() {
            @Override
            public Predicate toPredicate(Root<WeixinUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                try {
                    predicates.add(cb.lessThan(root.get(WeixinUser_.birthday), df.parse(WeixinUserGroup.Group_1.getEnd())));
                } catch (ParseException e) {
                    logger.error(e.getMessage(), e);
                }
                predicates.add(cb.equal(root.get(WeixinUser_.status), WeixinUserStatus.STATUS_1.getValue()));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
        long group1Sum4 = weixinUserRepository.count(new Specification<WeixinUser>() {
            @Override
            public Predicate toPredicate(Root<WeixinUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                try {
                    predicates.add(cb.lessThan(root.get(WeixinUser_.birthday), df.parse(WeixinUserGroup.Group_1.getEnd())));
                } catch (ParseException e) {
                    logger.error(e.getMessage(), e);
                }
                predicates.add(cb.equal(root.get(WeixinUser_.status), WeixinUserStatus.STATUS_2.getValue()));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });

        res.setGroup1(new long[]{group1Sum1, group1Sum2, group1Sum3, group1Sum4});

        long group2Sum1 = weixinUserRepository.count(new Specification<WeixinUser>() {
            @Override
            public Predicate toPredicate(Root<WeixinUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                try {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(WeixinUser_.birthday), df.parse(WeixinUserGroup.Group_2.getStart())));
                    predicates.add(cb.lessThan(root.get(WeixinUser_.birthday), df.parse(WeixinUserGroup.Group_2.getEnd())));
                } catch (ParseException e) {
                    logger.error(e.getMessage(), e);
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
        long group2Sum2 = weixinUserRepository.count(new Specification<WeixinUser>() {
            @Override
            public Predicate toPredicate(Root<WeixinUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                try {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(WeixinUser_.birthday), df.parse(WeixinUserGroup.Group_2.getStart())));
                    predicates.add(cb.lessThan(root.get(WeixinUser_.birthday), df.parse(WeixinUserGroup.Group_2.getEnd())));
                } catch (ParseException e) {
                    logger.error(e.getMessage(), e);
                }
                predicates.add(cb.equal(root.get(WeixinUser_.status), WeixinUserStatus.STATUS_0.getValue()));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
        long group2Sum3 = weixinUserRepository.count(new Specification<WeixinUser>() {
            @Override
            public Predicate toPredicate(Root<WeixinUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                try {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(WeixinUser_.birthday), df.parse(WeixinUserGroup.Group_2.getStart())));
                    predicates.add(cb.lessThan(root.get(WeixinUser_.birthday), df.parse(WeixinUserGroup.Group_2.getEnd())));
                } catch (ParseException e) {
                    logger.error(e.getMessage(), e);
                }
                predicates.add(cb.equal(root.get(WeixinUser_.status), WeixinUserStatus.STATUS_1.getValue()));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
        long group2Sum4 = weixinUserRepository.count(new Specification<WeixinUser>() {
            @Override
            public Predicate toPredicate(Root<WeixinUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                try {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(WeixinUser_.birthday), df.parse(WeixinUserGroup.Group_2.getStart())));
                    predicates.add(cb.lessThan(root.get(WeixinUser_.birthday), df.parse(WeixinUserGroup.Group_2.getEnd())));
                } catch (ParseException e) {
                    logger.error(e.getMessage(), e);
                }
                predicates.add(cb.equal(root.get(WeixinUser_.status), WeixinUserStatus.STATUS_2.getValue()));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });

        res.setGroup2(new long[]{group2Sum1, group2Sum2, group2Sum3, group2Sum4});

        long group3Sum1 = weixinUserRepository.count(new Specification<WeixinUser>() {
            @Override
            public Predicate toPredicate(Root<WeixinUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                try {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(WeixinUser_.birthday), df.parse(WeixinUserGroup.Group_3.getStart())));
                    predicates.add(cb.lessThan(root.get(WeixinUser_.birthday), df.parse(WeixinUserGroup.Group_3.getEnd())));
                } catch (ParseException e) {
                    logger.error(e.getMessage(), e);
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
        long group3Sum2 = weixinUserRepository.count(new Specification<WeixinUser>() {
            @Override
            public Predicate toPredicate(Root<WeixinUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                try {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(WeixinUser_.birthday), df.parse(WeixinUserGroup.Group_3.getStart())));
                    predicates.add(cb.lessThan(root.get(WeixinUser_.birthday), df.parse(WeixinUserGroup.Group_3.getEnd())));
                } catch (ParseException e) {
                    logger.error(e.getMessage(), e);
                }
                predicates.add(cb.equal(root.get(WeixinUser_.status), WeixinUserStatus.STATUS_0.getValue()));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
        long group3Sum3 = weixinUserRepository.count(new Specification<WeixinUser>() {
            @Override
            public Predicate toPredicate(Root<WeixinUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                try {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(WeixinUser_.birthday), df.parse(WeixinUserGroup.Group_3.getStart())));
                    predicates.add(cb.lessThan(root.get(WeixinUser_.birthday), df.parse(WeixinUserGroup.Group_3.getEnd())));
                } catch (ParseException e) {
                    logger.error(e.getMessage(), e);
                }
                predicates.add(cb.equal(root.get(WeixinUser_.status), WeixinUserStatus.STATUS_1.getValue()));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
        long group3Sum4 = weixinUserRepository.count(new Specification<WeixinUser>() {
            @Override
            public Predicate toPredicate(Root<WeixinUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                try {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(WeixinUser_.birthday), df.parse(WeixinUserGroup.Group_3.getStart())));
                    predicates.add(cb.lessThan(root.get(WeixinUser_.birthday), df.parse(WeixinUserGroup.Group_3.getEnd())));
                } catch (ParseException e) {
                    logger.error(e.getMessage(), e);
                }
                predicates.add(cb.equal(root.get(WeixinUser_.status), WeixinUserStatus.STATUS_2.getValue()));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });

        res.setGroup3(new long[]{group3Sum1, group3Sum2, group3Sum3, group3Sum4});

        long group4Sum1 = weixinUserRepository.count(new Specification<WeixinUser>() {
            @Override
            public Predicate toPredicate(Root<WeixinUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                try {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(WeixinUser_.birthday), df.parse(WeixinUserGroup.Group_4.getStart())));
                } catch (ParseException e) {
                    logger.error(e.getMessage(), e);
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
        long group4Sum2 = weixinUserRepository.count(new Specification<WeixinUser>() {
            @Override
            public Predicate toPredicate(Root<WeixinUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                try {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(WeixinUser_.birthday), df.parse(WeixinUserGroup.Group_4.getStart())));
                } catch (ParseException e) {
                    logger.error(e.getMessage(), e);
                }
                predicates.add(cb.equal(root.get(WeixinUser_.status), WeixinUserStatus.STATUS_0.getValue()));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
        long group4Sum3 = weixinUserRepository.count(new Specification<WeixinUser>() {
            @Override
            public Predicate toPredicate(Root<WeixinUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                try {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(WeixinUser_.birthday), df.parse(WeixinUserGroup.Group_4.getStart())));
                } catch (ParseException e) {
                    logger.error(e.getMessage(), e);
                }
                predicates.add(cb.equal(root.get(WeixinUser_.status), WeixinUserStatus.STATUS_1.getValue()));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
        long group4Sum4 = weixinUserRepository.count(new Specification<WeixinUser>() {
            @Override
            public Predicate toPredicate(Root<WeixinUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                try {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(WeixinUser_.birthday), df.parse(WeixinUserGroup.Group_4.getStart())));
                } catch (ParseException e) {
                    logger.error(e.getMessage(), e);
                }
                predicates.add(cb.equal(root.get(WeixinUser_.status), WeixinUserStatus.STATUS_2.getValue()));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });

        res.setGroup4(new long[]{group4Sum1, group4Sum2, group4Sum3, group4Sum4});

        return res;
    }

    public void saveWeixinUser(WeixinUser weixinUser) {
        weixinUserRepository.save(weixinUser);
    }

    public WeixinUser findWeixinUserByOpenId(String openId) {
        List<WeixinUser> weixinUsers = weixinUserRepository.findByOpenId(openId);
        return CollectionUtils.isNotEmpty(weixinUsers) ? weixinUsers.get(0) : null;
    }
}
