package com.henrique.nookio_api.shared.input;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public record InputPreSet(
        Integer page,
        Integer size,
        List<String> sort
) {

    public InputPreSet(Integer page, Integer size) {
        this(page, size, null);
    }

    public int pageNumber() {
        if (page == null || page < 0) return 0;
        return page;
    }

    public int pageSize() {
        return (size != null && size > 0) ? size : 20;
    }

    // Converte o 'sort' vindo do Front diretamente para o Sort do Spring Data
    public Sort toSort() {
        return toSort(Sort.unsorted());
    }

    // Converte o 'sort' do Front e aplica um defaultSort caso o Front não tenha mandado ordenação
    public Sort toSort(Sort defaultSort) {
        if (sort == null || sort.isEmpty()) {
            return defaultSort != null ? defaultSort : Sort.unsorted();
        }

        List<Sort.Order> orders = new ArrayList<>();
        for (String sortVal : sort) {
            if (sortVal == null || sortVal.isBlank()) continue;
            String[] parts = sortVal.split(",");
            if (parts.length > 0 && !parts[0].isBlank()) {
                String field = parts[0].trim();
                Sort.Direction direction = Sort.Direction.ASC;
                if (parts.length > 1 && parts[1].trim().equalsIgnoreCase("desc")) {
                    direction = Sort.Direction.DESC;
                }
                orders.add(new Sort.Order(direction, field));
            }
        }

        return orders.isEmpty() ? (defaultSort != null ? defaultSort : Sort.unsorted()) : Sort.by(orders);
    }

    // Suporta múltiplos Sort.Order padrões (ex: Sort.Order.asc("title"), Sort.Order.desc("createdAt"))
    public Sort toSort(Sort.Order... defaultOrders) {
        return toSort(defaultOrders != null && defaultOrders.length > 0 ? Sort.by(defaultOrders) : Sort.unsorted());
    }

    // Retorna o Pageable montado
    public Pageable pageable() {
        return PageRequest.of(pageNumber(), pageSize(), toSort());
    }

    // Retorna o Pageable montado com ordenação padrão caso o Front mande sort nulo
    public Pageable pageable(Sort defaultSort) {
        return PageRequest.of(pageNumber(), pageSize(), toSort(defaultSort));
    }

    // Retorna o Pageable montado com múltiplos Sort.Order padrões
    public Pageable pageable(Sort.Order... defaultOrders) {
        return PageRequest.of(pageNumber(), pageSize(), toSort(defaultOrders));
    }
}
