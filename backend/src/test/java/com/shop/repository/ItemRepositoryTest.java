package com.shop.repository;

import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.internal.verification.InOrderWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.test.context.TestPropertySource;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAQuery;
import com.shop.entity.QItem;
import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

import com.querydsl.core.BooleanBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

@SpringBootTest
//통합테스트를 위해 스프링에서 제공하는 어노테이션
@TestPropertySource(locations = "classpath:application-test.properties")
//테스트 코드 실행 시 application.properties에 설정한 값보다 application-test.properties에
//같은 설정이 있다면 더 높은 우선순위를 부여한다.
class ItemRepositoryTest {

    @PersistenceContext
    EntityManager em;
    // 영속성 컨텍스트를 사용하기 위해 @PersistenceContext 어노테이션을 이용해 EntityManager 빈을 주입한다.

    @Autowired
    ItemRepository itemRepository;
    //ItemRepository를 사용하기 위해서 @Autowired 어노테이션을 이용하여 Bean을 주입한다.
    @Test
    //테스트할 메서드 위에 선언하여 해당 메서드를 테스트 대상으로 지정한다.
    @DisplayName("상품 저장 테스트")
    //Junit5에 추가된 어노테이션으로 테스트 코드 실행 시 @DisplayName에 지정한 테스트명이 노출된다.
    public void createItemTest() {
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());

        Item savedItem = itemRepository.save(item);
        System.out.println(savedItem);
    }

    public void createItemList(){
        //테스트 코드 실행 시 데이터베이스에 상품 데이터가 없으므로 테스트 데이터 생성을 위해서
        //10개의 상품을 저장하는 메서드를 작성하여 findByItemNmTest()에서 실행해준다.
        for(int i=1; i<=10; i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            Item savedItem = itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품명 조회 테스트")
    public void findByItemNmTest(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemNm("테스트 상품1");
        //ItemRepository 인터페이스에 작성했던 findByItemNm 메서드를 호출한다.
        //파라미터로는 "테스트 상품1"이라는 상품명을 전달한다.
        for(Item item : itemList){
            System.out.println(item.toString());
            //조회 결과로 얻은 Item 객체들을 출력한다.
        }
    }

    @Test
    @DisplayName("상품명, 상품상세설명 or 테스트")
    public void findByItemNmOrItemDetailTest(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemNmOrItemDetail("테스트 상품1", "테스트 상품 상세 설명5");
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("가격 LessThan 테스트")
    public  void findByPriceLessThanTest(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByPriceLessThan(10005);
        for(Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("가격 내림차순 조회 테스트")
    public void findByPriceLessThanOrderByPriceDesc(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByPriceLessThanOrderByPriceDesc(10005);
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("@Query를 이용한 상품 조회 테스트")
    public void findByItemDetailTest(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemDetail("테스트 상품 상세 설명");
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("nativeQuery 속성을 이용한 상품 조회 테스트")
    public void findByItemDetailByNative(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemDetailByNative("테스트 상품 상세 설명");
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("Querydsl 조회테스트1")
    public void queryDslTest(){
        this.createItemList();
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        // JPAQueryFactory를 이용하여 쿼리를 동적으로 생성한다.
        // 생성자의 파라미터로는 EntityManager 객체를 넣어준다.
        QItem qItem = QItem.item;
        // Querydsl을 통한 쿼리를 생성하기 위해 플러그인을 통해 자동으로 생성된 QItem 객체를 이용한다.
        JPAQuery<Item> query = queryFactory.selectFrom(qItem)
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
                .where(qItem.itemDetail.like("%" + "테스트 상품 상세 설명" + "%"))
                .orderBy(qItem.price.desc());
        // 자바 소스코드지만 SQL문과 비슷하게 소스를 작성할 수 있다.

        List<Item> itemList = query.fetch();
        // JPAQuery 메서드중 하나인 fetch를 이용해서 쿼리 결과를 리스트로 반환한다.
        // fetch 메서드 실행 시점에 쿼리문이 실행된다.
        // JPAQuery에서 결과를 반환하는 메서드는 다음과 같다.
        /*
            List<T> fetch()                 : 조회 결과 리스트 반환
            T fetchOne                      : 조회 대상이 1건인 경우 제네릭으로 지정한 타입 반환
            T fetchFirst()                  : 조회 대상 중 1건만 반환
            Long fetchCount()               : 조회 대상 개수 반환
            QueryResult<T> fetchResults()   : 조회한 리스트와 전체 개수를 포함한 QueryResults 반환
         */


        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    public void createItemList2(){
    /*
    상품 데이터를 만드는 새로운 메소드를 만들겠습니다. 1번부터 5번 상품은 상품의 판매상태를 SELL(판매 중)
    으로 지정하고, 6번부터 10번 상품은 판매상태를 SOLID_OUT(품절)으로 세팅해 생성하겠습니다.
    */
        for(int i=1; i<=5; i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }

        for(int i=6; i<=10; i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);

            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
            item.setStockNumber(0);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품 Querydsl 조회 테스트 2")
    public void queryDslTest2(){
        this.createItemList2();

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        /*
        BooleanBuilder는 쿼리에 들어갈 조건을 만들어주는 빌더라고 생각하면 됩니다.
        Predicate를 구현하고 있으며 메소드 체인 형식으로 사용할 수 있습니다.
        */

        QItem item = QItem.item;
        String itemDetail = "테스트 상품 상세 설명";
        int price = 10003;
        String itemSellStat = "SELL";

        booleanBuilder.and(item.itemDetail.like("%" + itemDetail + "%"));
        /*
        필요한 상품을 조회하는데 필요한 "and"조건을 추가하고 있습니다.
        아래 소스에서 상품의 판매상태가 SELL일때만 booleanBuilder에서 판매상태 조건을
        동적으로 추가하는 것을 볼 수 있습니다.
        */
        booleanBuilder.and(item.price.gt(price));

        if(StringUtils.equals(itemSellStat, ItemSellStatus.SELL)){
            booleanBuilder.and(item.itemSellStatus.eq(ItemSellStatus.SELL));
        }

        Pageable pageable = PageRequest.of(0, 5);
        /*
        데이터를 페이징해 조회하도록 PageRequest.of() 메소드를 이용해 Pageble 객체를 생성합니다.
        첫번째 인자는 조회할 페이지의 번호, 두번째 인자는 한페이지당 조회할 데이터릐 개수를 넣어줍니다.
        */
        Page<Item> itemPagingResult = itemRepository.findAll(booleanBuilder, pageable);
        /*
        QuerydslPredicateExecutor 인터페이스에서 정의한 findAll() 메소드를 이용해
        조건에 맞는 데이터를 Page 객체로 받아옵니다.
        */
        System.out.println("total elements : " + itemPagingResult.getTotalElements());

        List<Item> resultItemList = itemPagingResult.getContent();
        for(Item resultItem: resultItemList){
            System.out.println(resultItem.toString());
        }

    }
}