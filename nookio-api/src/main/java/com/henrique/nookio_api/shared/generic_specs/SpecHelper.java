package com.henrique.nookio_api.shared.generic_specs;

import com.henrique.nookio_api.shared.input.Range;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class SpecHelper {

    public static <T, V> Specification<T> treatedSpec(V value, Function<V, Specification<T>> specSupplier) {
        if (value == null) {
            return null;
        }
        return specSupplier.apply(value);
    }

    public static <T> Specification<T> searchSpec(String searchTerm, List<String> fields) {
        return treatedSpec(
                searchTerm,
                spec -> (root, query, cb) -> {
                    if (searchTerm.isBlank() || fields == null || fields.isEmpty()) return cb.conjunction();

                    String likeTerm = "%" + searchTerm.toLowerCase() + "%";

                    Predicate[] predicates = fields.stream()
                            .map(field -> cb.like(cb.lower(getPath(root, field).as(String.class)), likeTerm))
                            .toArray(Predicate[]::new);

                    return cb.or(
                            predicates
                    );
        });
    }

    public static <T> Specification <T> eqSpec(Object equalsTerm, String field){
        return treatedSpec(
                equalsTerm,
                spec -> (root, query, cb) -> cb.equal(getPath(root, field), equalsTerm));
    }

    public static <T, V extends Collection<?>> Specification <T> inSpec(V inTerms, String field){
        return treatedSpec(
                inTerms,
                spec -> (root, query, cb) -> {
                    if (inTerms.isEmpty()) return cb.conjunction();
                    return getPath(root, field).in(inTerms);
                }
            );
    }

    public static <T, Y extends Comparable<?super Y>> Specification<T> rangeSpec(Y start, Y end, String field){
        Range range = Range.of(start, end);
        return treatedSpec(
                range,
                spec -> ((root, query, cb) -> {
                    if (range.start() != null && range.end() != null) {
                        return cb.between(getPath(root, field).as(Comparable.class), range.start(), range.end());
                    }
                    if (range.start() != null) {
                        return cb.greaterThanOrEqualTo(getPath(root, field).as(Comparable.class), range.start());
                    }
                    return cb.lessThanOrEqualTo(getPath(root, field).as(Comparable.class), range.end());
                })
        );
    }

    @SuppressWarnings("unchecked")
    public static <T, Y> Path<Y> getPath(Root<T> root, String fieldName) {
        if (fieldName == null || fieldName.isBlank()) throw new IllegalArgumentException("Field name cannot be empty");
        if (!fieldName.contains(".")) return (Path<Y>) root.get(fieldName);
        String[] parts = fieldName.split("\\.");
        Path<?> path = root.get(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            path = path.get(parts[i]);
        }
        return (Path<Y>) path;
    }
}
