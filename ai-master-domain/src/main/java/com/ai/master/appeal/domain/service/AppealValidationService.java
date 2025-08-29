package com.ai.master.appeal.domain.service;

import com.ai.master.appeal.domain.entity.Appeal;
import com.ai.master.appeal.domain.entity.AppealItem;
import com.ai.master.appeal.domain.enums.AppealItemStatus;
import com.ai.master.appeal.domain.enums.AppealType;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 申诉验证服务
 * 负责验证申诉数据的有效性
 */
@Service
public class AppealValidationService {
    
    /**
     * 验证申诉单
     */
    public void validateAppeal(Appeal appeal) {
        if (appeal == null) {
            throw new IllegalArgumentException("申诉单不能为空");
        }
        
        if (appeal.getAppealerId() == null) {
            throw new IllegalArgumentException("申诉方不能为空");
        }
        
        if (appeal.getReason() == null || appeal.getReason().trim().isEmpty()) {
            throw new IllegalArgumentException("申诉理由不能为空");
        }
        
        if (appeal.getReason().length() > 500) {
            throw new IllegalArgumentException("申诉理由不能超过500字符");
        }
    }
    
    /**
     * 验证申诉项
     */
    public void validateAppealItem(AppealItem item) {
        if (item == null) {
            throw new IllegalArgumentException("申诉项不能为空");
        }
        
        if (item.getType() == null) {
            throw new IllegalArgumentException("申诉类型不能为空");
        }
        
        if (item.getTitle() == null || item.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("申诉项标题不能为空");
        }
        
        if (item.getTitle().length() > 100) {
            throw new IllegalArgumentException("申诉项标题不能超过100字符");
        }
        
        if (item.getContent() == null || item.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("申诉内容不能为空");
        }
        
        if (item.getContent().length() > 1000) {
            throw new IllegalArgumentException("申诉内容不能超过1000字符");
        }
    }
    
    /**
     * 验证申诉类型
     */
    public void validateAppealType(AppealType type) {
        if (type == null) {
            throw new IllegalArgumentException("申诉类型不能为空");
        }
    }
    
    /**
     * 验证申诉项列表
     */
    public void validateAppealItems(List<AppealItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("申诉单必须包含至少一个申诉项");
        }
        
        items.forEach(this::validateAppealItem);
    }
    
    /**
     * 验证申诉状态转换
     */
    public void validateStatusTransition(Appeal appeal, AppealItemStatus newStatus) {
        // 这里可以添加状态转换的验证逻辑
        // 根据业务规则验证状态转换的合法性
    }
}