package com.ecommerce.services.products;
import com.ecommerce.common.enums.*;
import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
public class ProductService {
  private final ProductRepository repository;
  public ProductService(){this(new ProductRepository());}
  public ProductService(ProductRepository repository){this.repository=repository;}
  public Product addProduct(UserRole role,Product p)throws SQLException{
    if(role!=UserRole.ADMIN && role!=UserRole.SELLER)throw new IllegalArgumentException("Only Admin or Seller can add products.");
    validate(p);return repository.save(p);
  }
  public List<Product> getActiveProducts()throws SQLException{return repository.findAllActive();}
  public Product getProduct(long id)throws SQLException{return repository.findById(id).orElseThrow(()->new IllegalArgumentException("Product not found."));}
  public void changePrice(long id,BigDecimal price)throws SQLException{
    if(price==null||price.compareTo(BigDecimal.ZERO)<=0)throw new IllegalArgumentException("Price must be greater than zero.");
    repository.updatePrice(id,price);
  }
  public void changeStock(long id,int stock)throws SQLException{if(stock<0)throw new IllegalArgumentException("Stock cannot be negative.");repository.updateStock(id,stock);}
  public void changeCategory(long id,ProductCategory category)throws SQLException{
    if(category==null)throw new IllegalArgumentException("Category is required.");
    if(repository.hasAnyOrder(id))throw new IllegalStateException("Category cannot be changed after an order has been placed.");
    repository.updateCategory(id,category);
  }
  public void deleteProduct(long id)throws SQLException{
    if(repository.hasActiveOrder(id)||repository.hasAnyOrder(id))repository.deactivate(id);else repository.delete(id);
  }
  public boolean canAddToCart(long id)throws SQLException{return repository.findById(id).map(Product::isAvailableForCart).orElse(false);}
  private void validate(Product p){
    if(p==null||p.getName()==null||p.getName().isBlank())throw new IllegalArgumentException("Product name is required.");
    if(p.getCategory()==null)throw new IllegalArgumentException("Category is required.");
    if(p.getSellerId()==null||p.getSellerId()<=0)throw new IllegalArgumentException("Valid seller is required.");
    if(p.getPrice()==null||p.getPrice().compareTo(BigDecimal.ZERO)<=0)throw new IllegalArgumentException("Price must be greater than zero.");
    if(p.getStockQuantity()<0)throw new IllegalArgumentException("Stock quantity must be zero or more.");
  }
}
