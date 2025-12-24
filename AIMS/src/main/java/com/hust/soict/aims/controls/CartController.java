package com.hust.soict.aims.controls;

import com.hust.soict.aims.entities.CartItem;
import com.hust.soict.aims.entities.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CartController {
    private final List<CartItem> items = new ArrayList<>();
    private Consumer<Integer> changeListener;

    public void addProduct(Product p, int qty) {
        for (CartItem it : items) {
            if (it.getProduct().getId() == p.getId()) {
                it.setQuantity(it.getQuantity() + qty);
                notifyChanged();
                return;
            }
        }
        items.add(new CartItem(p, qty));
        notifyChanged();
    }

    public void updateQuantity(long productId, int qty) {
        items.removeIf(it -> {
            if (it.getProduct().getId() == productId) {
                if (qty <= 0) return true;
                it.setQuantity(qty);
            }
            return false;
        });
        notifyChanged();
    }

    public void remove(long productId) {
        items.removeIf(it -> it.getProduct().getId() == productId);
        notifyChanged();
    }

    public List<CartItem> getItems() { return new ArrayList<>(items); }

    public double getSubtotal() {
        return items.stream().mapToDouble(CartItem::getTotalPrice).sum();
    }

    public double getTotalWeight() {
        return items.stream().mapToDouble(CartItem::getTotalWeight).sum();
    }

    public void clear() { items.clear(); notifyChanged(); }

    public boolean isEmpty() { return items.isEmpty(); }

    public int getTotalQuantity() { return items.stream().mapToInt(CartItem::getQuantity).sum(); }

    public void setChangeListener(Consumer<Integer> listener) { this.changeListener = listener; }

    private void notifyChanged() { if (changeListener != null) changeListener.accept(getTotalQuantity()); }
}
