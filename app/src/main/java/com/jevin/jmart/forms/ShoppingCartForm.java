package com.jevin.jmart.forms;

public class ShoppingCartForm {

    private int shoppingCartId;
    private int productId;
    private int quantity;

    public ShoppingCartForm() {
    }

    public ShoppingCartForm(int shoppingCartId, int productId, int quantity) {
        this.shoppingCartId = shoppingCartId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public int getShoppingCartId() {
        return shoppingCartId;
    }

    public void setShoppingCartId(int shoppingCartId) {
        this.shoppingCartId = shoppingCartId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
