package com.shop.repository;

import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
//통합테스트를 위해 스프링에서 제공하는 어노테이션
@TestPropertySource(locations = "classpath:application-test.properties")
//테스트 코드 실행 시 application.properties에 설정한 값보다 application-test.properties에
//같은 설정이 있다면 더 높은 우선순위를 부여한다.
class ItemRepositoryTest {

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
}