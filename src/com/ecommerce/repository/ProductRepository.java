package com.ecommerce.repository;
import com.ecommerce.common.DatabaseConnection;
import com.ecommerce.common.enums.ProductCategory;
import com.ecommerce.common.enums.ProductStatus;
import com.ecommerce.model.Product;
import java.sql.*;
import java.util.*;
public class ProductRepository {
  public Product save(Product p) throws SQLException {
    String sql="INSERT INTO products(name,category,seller_id,price,stock_quantity) VALUES(?,?,?,?,?) RETURNING product_id,created_at,updated_at";
    try(Connection c=DatabaseConnection.getConnection(); PreparedStatement s=c.prepareStatement(sql)){
      s.setString(1,p.getName()); s.setString(2,p.getCategory().name()); s.setLong(3,p.getSellerId()); s.setBigDecimal(4,p.getPrice()); s.setInt(5,p.getStockQuantity());
      try(ResultSet r=s.executeQuery()){if(r.next()){p.setProductId(r.getLong(1));p.setCreatedAt(r.getTimestamp(2).toLocalDateTime());p.setUpdatedAt(r.getTimestamp(3).toLocalDateTime());}}
    } return p;
  }
  public Optional<Product> findById(long id) throws SQLException {
    try(Connection c=DatabaseConnection.getConnection();PreparedStatement s=c.prepareStatement("SELECT * FROM products WHERE product_id=?")){
      s.setLong(1,id);try(ResultSet r=s.executeQuery()){return r.next()?Optional.of(map(r)):Optional.empty();}
    }
  }
  public List<Product> findAllActive() throws SQLException {
    List<Product> out=new ArrayList<>();
    try(Connection c=DatabaseConnection.getConnection();PreparedStatement s=c.prepareStatement("SELECT * FROM products WHERE status='ACTIVE' ORDER BY product_id");ResultSet r=s.executeQuery()){while(r.next())out.add(map(r));}
    return out;
  }
  public void updatePrice(long id,java.math.BigDecimal price)throws SQLException{update("UPDATE products SET price=?,updated_at=CURRENT_TIMESTAMP WHERE product_id=? AND status='ACTIVE'",price,id);}
  public void updateStock(long id,int qty)throws SQLException{update("UPDATE products SET stock_quantity=?,updated_at=CURRENT_TIMESTAMP WHERE product_id=? AND status='ACTIVE'",qty,id);}
  public void updateCategory(long id,ProductCategory cat)throws SQLException{update("UPDATE products SET category=?,updated_at=CURRENT_TIMESTAMP WHERE product_id=? AND status='ACTIVE'",cat.name(),id);}
  private void update(String sql,Object value,long id)throws SQLException{
    try(Connection c=DatabaseConnection.getConnection();PreparedStatement s=c.prepareStatement(sql)){
      if(value instanceof java.math.BigDecimal b)s.setBigDecimal(1,b);else s.setObject(1,value);s.setLong(2,id);
      if(s.executeUpdate()==0)throw new SQLException("Active product not found.");
    }
  }
  public boolean hasAnyOrder(long id)throws SQLException{return exists("SELECT 1 FROM order_items WHERE product_id=? LIMIT 1",id);}
  public boolean hasActiveOrder(long id)throws SQLException{return exists("SELECT 1 FROM order_items oi JOIN orders o ON o.order_id=oi.order_id WHERE oi.product_id=? AND o.status IN ('PENDING','CONFIRMED','SHIPPED') LIMIT 1",id);}
  private boolean exists(String sql,long id)throws SQLException{
    try(Connection c=DatabaseConnection.getConnection();PreparedStatement s=c.prepareStatement(sql)){s.setLong(1,id);try(ResultSet r=s.executeQuery()){return r.next();}}
  }
  public void deactivate(long id)throws SQLException{
    try(Connection c=DatabaseConnection.getConnection();PreparedStatement s=c.prepareStatement("UPDATE products SET status='INACTIVE',updated_at=CURRENT_TIMESTAMP WHERE product_id=?")){s.setLong(1,id);if(s.executeUpdate()==0)throw new SQLException("Product not found.");}
  }
  public void delete(long id)throws SQLException{
    try(Connection c=DatabaseConnection.getConnection();PreparedStatement s=c.prepareStatement("DELETE FROM products WHERE product_id=?")){s.setLong(1,id);if(s.executeUpdate()==0)throw new SQLException("Product not found.");}
  }
  private Product map(ResultSet r)throws SQLException{
    Product p=new Product();p.setProductId(r.getLong("product_id"));p.setName(r.getString("name"));p.setCategory(ProductCategory.valueOf(r.getString("category")));
    p.setSellerId(r.getLong("seller_id"));p.setPrice(r.getBigDecimal("price"));p.setStockQuantity(r.getInt("stock_quantity"));p.setStatus(ProductStatus.valueOf(r.getString("status")));
    p.setCreatedAt(r.getTimestamp("created_at").toLocalDateTime());p.setUpdatedAt(r.getTimestamp("updated_at").toLocalDateTime());return p;
  }
}
