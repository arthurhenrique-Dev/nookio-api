package com.henrique.nookio_api.shared.chains;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseChain<T> {

    protected BaseChain<T> next;

    public void handle(T context) {
        step(context);
        if (next != null) {
            next.handle(context);
        }
    }

    public abstract void step(T context);
}
