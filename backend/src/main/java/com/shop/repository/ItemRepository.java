package com.shop.repository;

import com.querydsl.core.BooleanBuilder;
import com.shop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item> {
/*
    QuerydslPredicateExecutor<Item>{
    //QuerydslPredicateExecutor 인터페이스 상속을 추가한다.

    # QuerydslPredicateExecutor 인터페이스 정의 메소드
        1) long count(Predicate) : 조건에 맞는 데이터의 총 개수 반환
        2) boolean exists(Predicate) : 조건에 맞는 데이터가 존재하는지 여부 반환
        3) Iterable findAll(Predicate) : 조건에 맞는 모든 데이터 반환
        4) Page<T> findAll(Predicate, Pageable) : 조건에 맞는 모든 데이터를 페이징 처리하여 반환
        5) Iterable<T> findAll(Predicate, Sort) : 조건에 맞는 모든 데이터를 정렬하여 반환
        6) T findOne(Predicate) : 조건에 맞는 데이터 1개 반환
    }
 */
    List<Item> findByItemNm(String itemNm);
    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);
    List<Item> findByPriceLessThan(Integer price);
    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);

    @Query("select i from Item i where i.itemDetail like %:itemDetail% order by i.price desc")
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);

    @Query(value="select * from item i where i.item_detail like %:itemDetail% order by i.price desc", nativeQuery = true)
    List<Item> findByItemDetailByNative(@Param("itemDetail") String itemDetail);
}
