package com.loopers.domain.product;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Money {

    @Column(name = "price", nullable = false)
    private int value;

    public Money(int value) {
        if (value < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "가격은 음수일 수 없습니다.");
        }
        this.value = value;
    }

    public int value() {
        return value;
    }

    public Money add(Money other) {
        return new Money(this.value + other.value);
    }

    public Money multiply(int multiplier) {
        return new Money(this.value * multiplier);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money that)) return false;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
