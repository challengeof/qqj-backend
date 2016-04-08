package com.mishu.cgwy.common.service;

import com.mishu.cgwy.common.controller.FeedBackListRequest;
import com.mishu.cgwy.common.domain.*;
import com.mishu.cgwy.common.repository.FeedbackRepository;
import com.mishu.cgwy.common.repository.SuggestionRepository;
import com.mishu.cgwy.inventory.domain.Vendor_;
import com.mishu.cgwy.profile.domain.*;
import com.mishu.cgwy.profile.vo.FeedbackShowVo;
import com.mishu.cgwy.task.util.ExportExcelUtils;
import com.mishu.cgwy.utils.JpaQueryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.util.*;

/**
 * Created by bowen on 15-5-26.
 */
@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private SuggestionRepository suggestionRepository;
    @Autowired
    private EntityManager entityManager;

    public static final String FEEDBACK_LIST = "/template/feedback-list.xls";

    @Transactional
    public Feedback saveFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }


    @Transactional(readOnly = true)
    public HttpEntity<byte[]> exportFeedBackInfo(final FeedBackListRequest feedBackListRequest, List datas, String fileName, String template) throws Exception {
        Map<String, Object> beans = new HashMap<>();
        beans.put("list", datas);
        beans.put("submitFront", feedBackListRequest.getSubmitTimeFront());
        beans.put("submitBack", feedBackListRequest.getSubmitTimeBack());
        beans.put("now", new Date());

        return ExportExcelUtils.generateExcelBytes(beans, fileName, template);
    }

    public Page<FeedbackShowVo> findFeedBacks(FeedBackListRequest request) {

        final PageRequest pageRequest = new PageRequest(request.getPage(), request.getPageSize(),
                new Sort(request.isAsc() ? Sort.Direction.ASC : Sort.Direction.DESC, request.getSortField())
        );

        List<FeedbackShowVo> feedbackShowVos = JpaQueryUtils.valSelect(Feedback.class, new FeedBackQuerySpecification(request), entityManager, pageRequest, new JpaQueryUtils.SelectPathGetting<Feedback, List<FeedbackShowVo>, FeedBackQuerySpecification>() {
            @Override
            public Selection<?>[] getSelectPath(CriteriaBuilder cb, CriteriaQuery query, Root<Feedback> root, FeedBackQuerySpecification specification) {
                return new Selection<?>[]{

                        root.get(Feedback_.id),
                        root.get(Feedback_.feedbackDescription),
                        specification.getMediaFileJoin().get(MediaFile_.localPath),
                        specification.getMediaFileJoin().get(MediaFile_.qiNiuHash),
                        root.get(Feedback_.customer).get(Customer_.username),
                        specification.getRestaurantJoin().get(Restaurant_.id),
                        specification.getRestaurantJoin().get(Restaurant_.name),
                        specification.getRestaurantJoin().get(Restaurant_.receiver),
                        specification.getRestaurantJoin().get(Restaurant_.telephone),
                        root.get(Feedback_.submitTime),
                        root.get(Feedback_.status),
                        specification.getCityJoin().get(City_.id),
                        specification.getCityJoin().get(City_.name)

                };
            }

            @Override
            public List<FeedbackShowVo> resultWrappe(List<Tuple> tuples) {
                List<FeedbackShowVo> showVos = new ArrayList<FeedbackShowVo>();
                for (Tuple tuple : tuples) {
                    FeedbackShowVo fbsVo = new FeedbackShowVo();
                    fbsVo.setId(tuple.get(0, Long.class));
                    fbsVo.setFeedbackDescription(tuple.get(1, String.class));
                    fbsVo.setMediaFileurl(MediaFile.getUrl(tuple.get(2, String.class), tuple.get(3, String.class)));
                    fbsVo.setCustomerUsername(tuple.get(4, String.class));
                    fbsVo.setRestaurantId(tuple.get(5, Long.class));
                    fbsVo.setRestaurantName(tuple.get(6, String.class));
                    fbsVo.setReceiver(tuple.get(7, String.class));
                    fbsVo.setTelephone(tuple.get(8, String.class));
                    fbsVo.setSubmitTime(tuple.get(9, Date.class));
                    FeedbackStatus status = FeedbackStatus.fromInt(tuple.get(10, Integer.class));
                    fbsVo.setStatus(status);
                    fbsVo.setCityId(tuple.get(11, Long.class));
                    fbsVo.setCityName(tuple.get(12, String.class));
                    showVos.add(fbsVo);
                }
                return showVos;
            }
        });
        Long count = JpaQueryUtils.lineCount(Feedback.class, new FeedBackQuerySpecification(request), entityManager);
        Page<FeedbackShowVo> showVoPage = new PageImpl<FeedbackShowVo>(feedbackShowVos, pageRequest, count);
        return showVoPage;

//        return this.feedbackRepository.findAll(new FeedBackQuerySpecification(request), pageRequest);
    }

    public Feedback getOne(Long feedBackId) {
        return this.feedbackRepository.getOne(feedBackId);
    }

    private static class FeedBackQuerySpecification implements Specification<Feedback> {

        private FeedBackListRequest request;
        private Join<Feedback, MediaFile> mediaFileJoin;
        private ListJoin<Customer, Restaurant> restaurantJoin;
        private Join<Block, City> cityJoin;

        public Join<Block, City> getCityJoin() {
            return cityJoin;
        }

        public Join<Feedback, MediaFile> getMediaFileJoin() {
            return mediaFileJoin;
        }

        public ListJoin<Customer, Restaurant> getRestaurantJoin() {
            return restaurantJoin;
        }

        public FeedBackQuerySpecification(FeedBackListRequest request) {
            this.request = request;
        }

        @Override
        public Predicate toPredicate(Root<Feedback> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

            query.groupBy(root.get(Feedback_.id));
            restaurantJoin = root.join(Feedback_.customer).join(Customer_.restaurant);
            mediaFileJoin = root.join(Feedback_.file, JoinType.LEFT);
            cityJoin = root.join(Feedback_.customer).join(Customer_.block, JoinType.INNER).join(Block_.city, JoinType.INNER);

            List<Predicate> predicates = new ArrayList<>();
            if (null != request.getId()) {
                predicates.add(cb.equal(root.get(Feedback_.id), request.getId()));
            }
            if (null != request.getCityId()) {
                predicates.add(cb.equal(cityJoin.get(City_.id), request.getCityId()));
            }
            if (null != request.getRestaurantId()) {
                predicates.add(cb.equal(restaurantJoin.get(Restaurant_.id), request.getRestaurantId()));
            }
            if (null != request.getRestaurantName()) {
                predicates.add(cb.like(restaurantJoin.get(Restaurant_.name), "%" + request.getRestaurantName() + "%"));
            }
            if (null != request.getCustomerId()) {
                predicates.add(cb.equal(root.get(Feedback_.customer).get(Customer_.id), request.getCustomerId()));
            }
            if (null != request.getCustomerName()) {
                predicates.add(cb.like(root.get(Feedback_.customer).get(Customer_.username), "%" + request.getCustomerName() + "%"));
            }
            if (null != request.getVendorId()) {
                predicates.add(cb.equal(root.get(Feedback_.vendor).get(Vendor_.id), request.getVendorId()));
            }
            if (null != request.getVerdorName()) {
                predicates.add(cb.like(root.get(Feedback_.vendor).get(Vendor_.username), "%" + request.getVerdorName() + "%"));
            }
            if (null != request.getStatus()) {
                predicates.add(cb.equal(root.get(Feedback_.status), request.getStatus()));
            }
            if (null != request.getType()) {
                predicates.add(cb.equal(root.get(Feedback_.status), request.getType()));
            }
            if (null != request.getSubmitTimeFront()) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(Feedback_.submitTime), request.getSubmitTimeFront()));
            }
            if (null != request.getSubmitTimeBack()) {
                predicates.add(cb.lessThan(root.get(Feedback_.submitTime), request.getSubmitTimeBack()));
            }
            if (null != request.getUpdateTimeFront()) {
                predicates.add(cb.lessThan(root.get(Feedback_.updateTime), request.getUpdateTimeFront()));
            }
            if (null != request.getUpdateTimeBack()) {
                predicates.add(cb.lessThan(root.get(Feedback_.updateTime), request.getUpdateTimeBack()));
            }
            return cb.and(predicates.toArray(new Predicate[]{}));
        }


    }

    public Suggestion saveSuggestion(Suggestion suggestion) {
        return suggestionRepository.save(suggestion);
    }

    public Page<Suggestion> getSuggestionList(final FeedBackListRequest request) {
        return suggestionRepository.findAll(new Specification<Suggestion>() {
            @Override
            public Predicate toPredicate(Root<Suggestion> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                if (request.getId() != null) {
                    cb.and(cb.equal(root.get(Suggestion_.restaurant).get(Restaurant_.customer).get(Customer_.city).get(City_.id), request.getId()));
                }
                cb.desc(root.get(Suggestion_.id));
                return cb.and();
            }
        }, new PageRequest(request.getPage(), request.getPageSize()));
    }

}
