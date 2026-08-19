package com.henrique.nookio_api.shared.input;

public record Range<T extends Comparable<? super T>>(
        T start,
        T end
) {
    public static <T extends Comparable<? super T>> Range<T> of(T start, T end) {
        if (start == null && end == null) {
            return null;
        }
        return new Range<>(start, end);
    }

    public static <T extends Comparable<? super T>> Range<T> of(
            T start,
            T end,
            T defaultStart,
            T defaultEnd
    ) {
        T definitiveStart = (start != null) ? start : defaultStart;
        T definitiveEnd = (end != null) ? end : defaultEnd;

        if (definitiveStart == null && definitiveEnd == null) {
            return null;
        }

        return new Range<>(definitiveStart, definitiveEnd);
    }
}