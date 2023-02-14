package com.restaurant.restapi.controllers;

import com.restaurant.restapi.models.Product;
import com.restaurant.restapi.repositories.ProductRepository;
import com.restaurant.restapi.service.ImageUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductRepository productRepository;
    private  final ImageUploadService uploadService;

    public ProductController(ProductRepository productRepository, ImageUploadService uploadService) {
        this.productRepository = productRepository;
        this.uploadService = uploadService;
    }

    @GetMapping
    public ResponseEntity<Iterable<Product>>  getProducts(){
        return new ResponseEntity<>( productRepository.findAll(Sort.by(Sort.Direction.ASC, "id")), HttpStatus.OK) ;
    }
    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@RequestHeader("Authorization") String auth, @RequestParam("title") String title, @RequestParam("detail") String detail,
                                                 @RequestParam("category") String category, @RequestParam("price") Float price, @RequestParam("stock") Integer stock, Optional<MultipartFile> img) {
        Product product = new Product();
        if(img.isPresent()){
            product.setImg(uploadService.save(img.get()));
        }else{
            return  new ResponseEntity<String>("No Image given", HttpStatus.NOT_FOUND);
        }
        product.setAlt(title);
        product.setTitle(title);
        product.setDetail(detail);
        product.setCategory(category);
        product.setPrice(price);
        product.setStock(stock);
        productRepository.save(product);
    return new ResponseEntity<Product>(product, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable("id") Long productId ) {
        if(productRepository.existsById(productId)){

            return new ResponseEntity<>(productRepository.findById(productId), HttpStatus.ACCEPTED);
        }else{
            return  new ResponseEntity<>("Something Went Wrong", HttpStatus.NOT_FOUND);
        }
    }
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteProduct(@RequestParam("productsArr") List<Long> productsArr) {
        System.out.print(productsArr);
      productsArr.forEach((id) -> {
            if(productRepository.existsById(id)){
                productRepository.deleteById(id);
            }
        } );
        return new ResponseEntity<>( "Deleted" , HttpStatus.ACCEPTED);

    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProduct(@RequestHeader("Authorization") String auth, @RequestParam("id") Long Id, @RequestParam("title") String title, @RequestParam("detail") String detail,
                                           @RequestParam("category") String category, @RequestParam("price") Float price, @RequestParam("stock") Integer stock, Optional<MultipartFile> img) {
        Optional<Product> currentProduct = productRepository.findById(Id);
        if(currentProduct.isPresent()){
            if(img.isPresent()){
                currentProduct.get().setImg(uploadService.save(img.get()));
            }
            currentProduct.get().setAlt(title);
            currentProduct.get().setTitle(title);
            currentProduct.get().setDetail(detail);
            currentProduct.get().setCategory(category);
            currentProduct.get().setPrice(price);
            currentProduct.get().setStock(stock);

            Product updatedProduct = productRepository.save(currentProduct.get());
            return new ResponseEntity<Product>( updatedProduct , HttpStatus.ACCEPTED);
        }else{
            return  new ResponseEntity<>("Something Went Wrong", HttpStatus.NOT_FOUND);

        }
    }
}
