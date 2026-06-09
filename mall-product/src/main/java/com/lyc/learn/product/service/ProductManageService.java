package com.lyc.learn.product.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lyc.learn.product.entity.ProductDetail;
import com.lyc.learn.product.entity.ProductImage;
import com.lyc.learn.product.entity.SeckillProduct;
import com.lyc.learn.common.exception.MallException;
import com.lyc.learn.common.utils.JsonUtil;
import com.lyc.learn.common.utils.MallUtil;
import com.lyc.learn.product.vo.AddSeckillProductVo;
import com.lyc.learn.product.vo.SeckillProductDetailVo;
import com.lyc.learn.product.vo.SeckillProductListVo;
import com.lyc.learn.product.mapper.ProductDetailMapper;
import com.lyc.learn.product.mapper.ProductImageMapper;
import com.lyc.learn.product.mapper.SeckillProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ProductManageService {

    @Autowired
    SeckillProductMapper productMapper;

    @Autowired
    ProductImageMapper productImageMapper;

    @Autowired
    ProductDetailMapper productDetailMapper;
    
    @Autowired
    OssClientService ossClientService;

    @Transactional(rollbackFor = Exception.class)
    public SeckillProduct addSeckillProduct(AddSeckillProductVo vo) {
        // 转为product对象
        SeckillProduct product = JsonUtil.obj2obj(vo, SeckillProduct.class);
        if (product == null) {
            throw new MallException("创建商品失败，传递参数为空");
        }
        // 剩余库存等于初始库存
        product.setStockCount(vo.getInitStock());
        
        // 启用
        product.setStatus(1);
        
        // 主图
        product.setMainImgUrl(vo.getImages().get(0));

        int insResult = productMapper.insert(product);
        if (insResult != 1) {
            throw new MallException("创建商品失败");
        }
        
        if (vo.getImages().size() > 1) {
            // 保存详细图片
            List<ProductImage> productImageList = new ArrayList<>();
            for (int i = 1; i < vo.getImages().size(); i++) {
                String url = vo.getImages().get(i);
                ProductImage productImage = new ProductImage();
                productImage.setProductId(product.getId());
                productImage.setImageUrl(url);
                productImage.setImageType(1);
                productImage.setSortOrder(i);
                productImageList.add(productImage);
            }
            productImageMapper.insert(productImageList);
        }
        
        if (MallUtil.isNotEmpty(vo.getDescription())) {
            // 保存商品详细描述
            ProductDetail productDetail = new ProductDetail();
            productDetail.setProductId(product.getId());
            productDetail.setContent(vo.getDescription());
            productDetailMapper.insert(productDetail);
        }

        return product;
    }

    public List<SeckillProductListVo> getSeckillProductList() {
        List<SeckillProduct> productList = productMapper.selectList(new LambdaQueryWrapper<SeckillProduct>().ne(SeckillProduct::getStatus, 0));
        if (MallUtil.isEmpty(productList)) return Collections.emptyList();
        
        List<SeckillProductListVo> seckillProductListVoList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (SeckillProduct product : productList) {
            SeckillProductListVo vo = new SeckillProductListVo();
            vo.setId(product.getId());
            vo.setProductName(product.getProductName());
            vo.setOriginalPrice(product.getOriginalPrice());
            vo.setSeckillPrice(product.getSeckillPrice());
            vo.setStartTime(product.getStartTime().format(formatter));
            vo.setEndTime(product.getEndTime().format(formatter));
            // 计算折扣
            BigDecimal discount = product.getSeckillPrice().multiply(BigDecimal.TEN)
                    .divide(product.getOriginalPrice(), 1, RoundingMode.HALF_UP);
            vo.setDiscount(discount);
            
            // 申请图片临时查看权限
            String url = null;
            try {
                url = ossClientService.generatePresignedUrl(product.getMainImgUrl());
            } catch (URISyntaxException e) {
                throw new MallException(e.getMessage());
            }
            vo.setImage(url);
            
            // 判断状态
            String status = "active";
            if (product.getStatus() == 0) {
                status = "end";
            } else {
                if (product.getEndTime().isBefore(LocalDateTime.now())) {
                    status = "end";
                } else if (product.getStartTime().isAfter(LocalDateTime.now())) {
                    status = "upcoming";
                }
            }
            vo.setStatus(status);
            vo.setSummary(product.getSummary());

            seckillProductListVoList.add(vo);
        }
        
        return seckillProductListVoList;
    }

    public SeckillProductDetailVo getSeckillProductDetail(Long productId) {
        SeckillProduct product = productMapper.selectById(productId);
        List<ProductImage> productImages = productImageMapper.selectList(new LambdaQueryWrapper<ProductImage>()
                .eq(ProductImage::getProductId, productId)
                .orderByAsc(ProductImage::getSortOrder));
        ProductDetail productDetail = productDetailMapper.selectOne(new LambdaQueryWrapper<ProductDetail>()
                .eq(ProductDetail::getProductId, productId));
        SeckillProductDetailVo result = JsonUtil.obj2obj(product, SeckillProductDetailVo.class);
        List<String> images = new ArrayList<>();
        try {
            String url = ossClientService.generatePresignedUrl(product.getMainImgUrl());
            images.add(url);
            for (ProductImage productImage : productImages) {
                images.add(ossClientService.generatePresignedUrl(productImage.getImageUrl()));
            }
        } catch (URISyntaxException e) {
            throw new MallException(e.getMessage());
        }
        result.setImages(images);
        result.setDescription(productDetail.getContent());

        // 计算折扣
        BigDecimal discount = product.getSeckillPrice().multiply(BigDecimal.TEN)
                .divide(product.getOriginalPrice(), 1, RoundingMode.HALF_UP);
        result.setDiscount(discount);
        // 判断状态
        String status = "active";
        if (product.getStatus() == 0) {
            status = "end";
        } else {
            if (product.getEndTime().isBefore(LocalDateTime.now())) {
                status = "end";
            } else if (product.getStartTime().isAfter(LocalDateTime.now())) {
                status = "upcoming";
            }
        }
        result.setStatus(status);
        
        result.setStock(product.getStockCount());
        result.setSales(product.getInitStock() - product.getStockCount());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        result.setStartTimeStr(product.getStartTime().format(formatter));
        result.setEndTimeStr(product.getEndTime().format(formatter));
        return result;
    }
}
