package com.henrique.nookio_api.shared.chains;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseChain2<T, U> {

    protected BaseChain2<T, U> next;

    public void handle(T param1, U param2) {
        step(param1, param2);
        if (next != null) {
            next.handle(param1, param2);
        }
    }

    public abstract void step(T param1, U param2);
}
