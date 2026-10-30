package com.ecommerce.model;
import com.ecommerce.common.enums.ProductCategory;
import com.ecommerce.common.enums.ProductStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public class Product {
  private Long productId;
  private String name;
  private ProductCategory category;
  private Long sellerId;
  private BigDecimal price;
  private int stockQuantity;
  private ProductStatus status = ProductStatus.ACTIVE;
  private LocalDateTime createdAt, updatedAt;
  public Product() {}
  public Product(String name, ProductCategory category, Long sellerId, BigDecimal price, int stockQuantity) {
    this.name=name; this.category=category; this.sellerId=sellerId; this.price=price; this.stockQuantity=stockQuantity;
  }
  public Long getProductId(){return productId;} public void setProductId(Long v){productId=v;}
  public String getName(){return name;} public void setName(String v){name=v;}
  public ProductCategory getCategory(){return category;} public void setCategory(ProductCategory v){category=v;}
  public Long getSellerId(){return sellerId;} public void setSellerId(Long v){sellerId=v;}
  public BigDecimal getPrice(){return price;} public void setPrice(BigDecimal v){price=v;}
  public int getStockQuantity(){return stockQuantity;} public void setStockQuantity(int v){stockQuantity=v;}
  public ProductStatus getStatus(){return status;} public void setStatus(ProductStatus v){status=v;}
  public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){createdAt=v;}
  public LocalDateTime getUpdatedAt(){return updatedAt;} public void setUpdatedAt(LocalDateTime v){updatedAt=v;}
  public ProductStatus getDisplayStatus(){
    if(status==ProductStatus.INACTIVE) return ProductStatus.INACTIVE;
    return stockQuantity==0 ? ProductStatus.OUT_OF_STOCK : ProductStatus.ACTIVE;
  }
  public boolean isAvailableForCart(){return status==ProductStatus.ACTIVE && stockQuantity>0;}
  @Override public String toString(){return "Product{id="+productId+", name='"+name+"', category="+category+", price="+price+", stock="+stockQuantity+", status="+getDisplayStatus()+"}";}
}
