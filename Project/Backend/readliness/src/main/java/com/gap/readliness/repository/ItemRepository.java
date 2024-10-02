package com.gap.readliness.repository;

import com.gap.readliness.dto.ItemResponsePage;
import com.gap.readliness.model.Item;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = " SELECT items_id AS itemsId, items_name AS itemsName, items_code AS itemsCode, \n" +
            "       stock, price, is_available AS isAvailable, last_re_stock AS lastReStock \n" +
            "       FROM items", nativeQuery = true)
    Page<ItemResponsePage> getItem(Pageable pageable);

    Item findByItemsId(@Param("itemsId") Long itemsId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM items c WHERE c.items_id = :itemsId", nativeQuery = true)
    int deleteByItemsId(@Param("itemsId") Long itemsId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE items SET items_name = :itemsName, items_code = :itemsCode, stock = :stock, price = :price, is_available = :isAvailable, last_re_stock = :lastReStock WHERE items_id = :itemsId", nativeQuery = true)
    int updateItem(@Param("itemsName") String itemsName,
                   @Param("itemsCode") String itemsCode,
                   @Param("stock") Integer stock,
                   @Param("price") BigDecimal price,
                   @Param("isAvailable") Integer isAvailable,
                   @Param("lastReStock") Date lastReStock,
                   @Param("itemsId") Long itemsId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Item b WHERE b.itemsId = :itemsId AND b.stock > 0 AND b.isAvailable = 1")
    Optional<Item> findByIdWithLock(@Param("itemsId") Long itemsId);

    @Query("SELECT MAX(i.itemsCode) FROM Item i")
    Optional<String> findMaxItemCode();

}
