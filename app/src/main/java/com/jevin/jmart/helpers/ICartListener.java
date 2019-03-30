package com.jevin.jmart.helpers;

import com.jevin.jmart.models.CartProduct;

public interface ICartListener {

    void itemAdded(CartProduct cartProduct);

    void itemUpdated(CartProduct cartProduct);

    void itemRemoved(CartProduct cartProduct);

    void cartCleared();

    void cartCheckOut();
}
