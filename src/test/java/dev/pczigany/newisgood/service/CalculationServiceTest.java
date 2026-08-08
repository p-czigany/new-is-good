package dev.pczigany.newisgood.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

class CalculationServiceTest {

    private final CalculationService calculationService = new CalculationService();

    @Nested
    class WithDivision {

        @Test
        void shouldCalculateProductsExceptCurrentElement() {
            List<Long> result = calculationService.calculateA(List.of(1, 2, 3, 4));

            assertThat(result).containsExactly(24L, 12L, 8L, 6L);
        }

        @Test
        void shouldHandleOneZero() {
            List<Long> result = calculationService.calculateA(List.of(1, 2, 0, 4));

            assertThat(result).containsExactly(0L, 0L, 8L, 0L);
        }

        @Test
        void shouldHandleMultipleZeros() {
            List<Long> result = calculationService.calculateA(List.of(1, 0, 3, 0));

            assertThat(result).containsExactly(0L, 0L, 0L, 0L);
        }

        @Test
        void shouldHandleNegativeNumbers() {
            List<Long> result = calculationService.calculateA(List.of(-1, 2, -3, 4));

            assertThat(result).containsExactly(-24L, 12L, -8L, 6L);
        }

        @Test
        void shouldReturnOneForSingleElementInput() {
            List<Long> result = calculationService.calculateA(List.of(5));

            assertThat(result).containsExactly(1L);
        }
    }

    @Nested
    class WithoutDivision {

        @Nested
        class QuadraticComplexity {

            @Test
            void shouldCalculateSolutionBWithoutDivision() {
                List<Long> result = calculationService.calculateB(List.of(1, 2, 3, 4));

                assertThat(result).containsExactly(24L, 12L, 8L, 6L);
            }

            @Test
            void solutionBShouldHandleZeros() {
                assertThat(calculationService.calculateB(List.of(1, 2, 0, 4)))
                        .containsExactly(0L, 0L, 8L, 0L);
            }
        }

        @Nested
        class LinearComplexity {

            @Test
            void shouldCalculateSolutionCWithoutDivisionInLinearTime() {
                List<Long> result = calculationService.calculateC(List.of(1, 2, 3, 4));

                assertThat(result).containsExactly(24L, 12L, 8L, 6L);
            }

            @Test
            void solutionCShouldHandleOneZero() {
                List<Long> result = calculationService.calculateC(List.of(1, 2, 0, 4));

                assertThat(result).containsExactly(0L, 0L, 8L, 0L);
            }

            @Test
            void solutionCShouldHandleMultipleZeros() {
                List<Long> result = calculationService.calculateC(List.of(1, 0, 3, 0));

                assertThat(result).containsExactly(0L, 0L, 0L, 0L);
            }

            @Test
            void solutionCShouldHandleNegativeNumbers() {
                List<Long> result = calculationService.calculateC(List.of(-1, 2, -3, 4));

                assertThat(result).containsExactly(-24L, 12L, -8L, 6L);
            }

            @Test
            void solutionCShouldHandleSingleElementInput() {
                List<Long> result = calculationService.calculateC(List.of(5));

                assertThat(result).containsExactly(1L);
            }
        }
    }
}
