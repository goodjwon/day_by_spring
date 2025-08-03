package com.example.spring.exception;

/**
 * 엔티티를 찾을 수 없을 때 발생하는 예외
 */
public class EntityNotFoundException extends BusinessException {
    
    public EntityNotFoundException(String entityName, Long id) {
        super("ENTITY_NOT_FOUND", entityName + "을(를) 찾을 수 없습니다. ID: " + id);
    }
    
    public EntityNotFoundException(String message) {
        super("ENTITY_NOT_FOUND", message);
    }
}