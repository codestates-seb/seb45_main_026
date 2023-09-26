package com.server.domain.adjustment.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.domain.adjustment.domain.Adjustment;
import com.server.domain.adjustment.repository.dto.AdjustmentData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

import static com.server.domain.adjustment.domain.QAdjustment.*;
import static com.server.domain.order.entity.QOrderVideo.orderVideo;
import static com.server.domain.video.entity.QVideo.video;

public class AdjustmentRepositoryImpl implements AdjustmentRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public AdjustmentRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
        this.em = em;
    }

    @Override
    public Page<AdjustmentData> findByPeriod(Long memberId, Pageable pageable, Integer month, Integer year, String sort) {

        TypedQuery<Object[]> jpqlQuery = em.createQuery(
                        "SELECT v.videoId, " +
                                "v.videoName, " +
                                "SUM(ov.price) AS totalSaleAmount, " +
                                "SUM(CASE WHEN ov.orderStatus = 'CANCELED' THEN ov.price ELSE 0 END) AS refundAmount " +
                                "FROM Video v " +
                                "LEFT JOIN v.orderVideos ov " +
                                "LEFT JOIN ov.order o " +
                                "WHERE o.paymentKey != null " +
                                "AND v.channel.id = :memberId " +
                                getDateCondition(month, year) +
                                "GROUP BY v.videoId " +
                                getAdjustmentSort(sort), Object[].class)
                .setParameter("memberId", memberId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());

        setDateCondition(month, year, jpqlQuery);

        List<Object[]> resultList = jpqlQuery.getResultList();

        List<AdjustmentData> videoReportDatas = resultList.stream()
                .map(arr -> new AdjustmentData(
                        (Long) arr[0],
                        (String) arr[1],
                        ((Number) arr[2]).intValue(),
                        ((Number) arr[3]).intValue()
                ))
                .collect(Collectors.toList());

        JPAQuery<Long> countQuery = queryFactory.select(video.count())
                .from(video)
                .join(video.orderVideos, orderVideo)
                .where(video.channel.channelId.eq(memberId));

        return new PageImpl<>(videoReportDatas, pageable, countQuery.fetchOne());
    }

    @Override
    public Integer calculateAmount(Long memberId, Integer month, Integer year) {

        TypedQuery<Long> jpqlCountQuery = em.createQuery(
                        "SELECT SUM(ov.price) - SUM(CASE WHEN ov.orderStatus = 'CANCELED' THEN ov.price ELSE 0 END) AS total " +
                                "FROM Video v " +
                                "JOIN v.orderVideos ov " +
                                "JOIN ov.order o " +
                                "WHERE o.paymentKey != null " +
                                "AND v.channel.id = :memberId " +
                                getDateCondition(month, year), Long.class)
                .setParameter("memberId", memberId);

        setDateCondition(month, year, jpqlCountQuery);

        Long singleResult = jpqlCountQuery.getSingleResult();

        return singleResult == null ? 0 : singleResult.intValue();
    }

    @Override
    public List<Adjustment> findMonthlyData(Long memberId, Integer year) {

        return queryFactory.selectFrom(adjustment)
                .where(adjustment.member.memberId.eq(memberId)
                        .and(YearEq(year)))
                .fetch();
    }

    private BooleanExpression YearEq(Integer year) {

        if(year == null) return null;

        return adjustment.adjustmentYear.eq(year);
    }

    private String getDateCondition(Integer month, Integer year) {

        String dateCondition = "";

        if (year != null) {
            dateCondition += "AND FUNCTION('YEAR', o.completedDate) = :year ";
        }

        if (month != null) {
            dateCondition += "AND FUNCTION('MONTH', o.completedDate) = :month ";
        }

        return dateCondition;
    }

    private void setDateCondition(Integer month, Integer year, TypedQuery jpqlQuery) {

        if (year != null) {
            jpqlQuery.setParameter("year", year);
        }

        if (month != null) {
            jpqlQuery.setParameter("month", month);
        }
    }

    private String getAdjustmentSort(String sort) {

        String order = "ORDER BY ";

        if (sort.equals("totalSaleAmount")) {
            order += "totalSaleAmount DESC, ";
        } else if (sort.equals("refundAmount")) {
            order += "refundAmount DESC, ";
        }

        return order + "v.createdDate DESC";
    }
}
