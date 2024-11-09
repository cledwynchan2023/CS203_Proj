package com.codewithcled.fullstack_backend_proj1.UnitTests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;


import com.codewithcled.fullstack_backend_proj1.service.EloRatingServiceImplementation;

@ExtendWith(MockitoExtension.class)
public class EloRatingServiceTest {

    @InjectMocks
    private EloRatingServiceImplementation eloRatingService;

    private static final int VALID_ELO = 1000;
    private static final int INVALID_ELO = -1;
    private static final int HIGH_ELO = 2731;
    private static final int LOW_ELO = 515;
    private static final int VERY_LOW_ELO = 5;
    private static final int HIGH_DIFFERENCE_ELO = 2800;

    @Test
    void isValidElo_Success_ReturnTrue() {
        // Given
        int elo = VALID_ELO;

        // When
        boolean result = eloRatingService.isValidElo(elo);

        // Then
        assertTrue(result);
    }

    @Test
    void isValidElo_Fail_ReturnFalse() {
        // Given
        int elo = INVALID_ELO;

        // When
        boolean result = eloRatingService.isValidElo(elo);

        // Then
        assertFalse(result);
    }

    @Test
    void getKValue_SuccessForElo2731_Return11() {
        // Given
        int elo = HIGH_ELO;

        // When
        int result = eloRatingService.getKValue(elo);

        // Then
        assertEquals(11, result);
    }

    @Test
    void getKValue_SuccessForElo515_Return39() {
        // Given
        int elo = LOW_ELO;

        // When
        int result = eloRatingService.getKValue(elo);

        // Then
        assertEquals(39, result);
    }

    @Test
    void getKValue_SuccessForElo514_Return40() {
        // Given
        int elo = 514;

        // When
        int result = eloRatingService.getKValue(elo);

        // Then
        assertEquals(40, result);
    }

    @Test
    void getKValue_SuccessForEloAbove2731_Return10() {
        // Given
        int elo = HIGH_ELO + 1;

        // When
        int result = eloRatingService.getKValue(elo);

        // Then
        assertEquals(10, result);
    }

    @Test
    void WinProbabilityOnElo_Success_Return0_5() {
        // Given
        int elo1 = VALID_ELO;
        int elo2 = VALID_ELO;

        // When
        double result = eloRatingService.WinProbabilityOnElo(elo1, elo2);

        // Then
        assertEquals(0.5, result, 0.0001);
    }

    @Test
    void WinValue_SuccessForOutcome1_Return1() {
        // Given
        int outcome = -1;

        // When
        double result = eloRatingService.WinValue(outcome);

        // Then
        assertEquals(1, result);
    }

    @Test
    void WinValue_SuccessForOutcome2_Return0() {
        // Given
        int outcome = 1;

        // When
        double result = eloRatingService.WinValue(outcome);

        // Then
        assertEquals(0, result);
    }

    @Test
    void WinValue_SuccessForDrawOutcome_Return0_5() {
        // Given
        int outcome = 0;

        // When
        double result = eloRatingService.WinValue(outcome);

        // Then
        assertEquals(0.5, result);
    }

    @Test
    void WinValue_FailForInvalidOutcome_ReturnNegative1() {
        // Given
        int outcome = 123;

        // When
        double result = eloRatingService.WinValue(outcome);

        // Then
        assertEquals(-1, result);
    }

    @Test
    void EloCalculation_SuccessForBothElo5_Return0_0() {
        // Given
        int elo1 = VERY_LOW_ELO;
        int elo2 = VERY_LOW_ELO;
        int outcome = 1;

        // When
        double result = eloRatingService.EloCalculation(elo1, elo2, outcome);

        // Then
        assertEquals(0.0, result);
    }

    @Test
    void EloCalculation_FailForInvalidElo1_ReturnIllegalArgumentException() {
        // Given
        int elo1 = INVALID_ELO;
        int elo2 = VALID_ELO;
        int outcome = 1;

        // When / Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> 
            eloRatingService.EloCalculation(elo1, elo2, outcome)
        );
        assertEquals("Invalid elo values", thrown.getMessage());
    }

    @Test
    void EloCalculation_FailForInvalidElo2_ReturnIllegalArgumentException() {
        // Given
        int elo1 = VALID_ELO;
        int elo2 = INVALID_ELO;
        int outcome = 1;

        // When / Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> 
            eloRatingService.EloCalculation(elo1, elo2, outcome)
        );
        assertEquals("Invalid elo values", thrown.getMessage());
    }

    @Test
    void EloCalculation_FailForInvalidOutcome_ReturnIllegalArgumentException() {
        // Given
        int elo1 = VALID_ELO;
        int elo2 = VALID_ELO;
        int outcome = 12;

        // When / Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> 
            eloRatingService.EloCalculation(elo1, elo2, outcome)
        );
        assertEquals("Invalid match result only accepts -1 for A wins, 1 for B wins and 0 for draw", thrown.getMessage());
    }

    @Test
    void EloCalculationForBoth_Success_ReturnUpdatedElos() throws IllegalArgumentException {
        // Given
        int elo1 = HIGH_DIFFERENCE_ELO;
        int elo2 = HIGH_DIFFERENCE_ELO;
        int outcome = 1;

        // When
        List<Double> result = eloRatingService.eloRatingForBoth(elo1, elo2, outcome);

        // Then
        assertEquals(2795, result.get(0));
        assertEquals(2805, result.get(1));
    }

    @Test
    void EloChange_Success_ReturnPositiveValue() {
        // Given
        int k = 20;
        double winProbability = 0.5;
        double winValue = 1;

        // When
        double result = eloRatingService.eloChange(k, winValue, winProbability);

        // Then
        assertEquals(10, result);
    }

    @Test
    void EloChange_SuccessForZeroWinValue_ReturnNegativeValue() {
        // Given
        int k = 20;
        double winProbability = 0.5;
        double winValue = 0;

        // When
        double result = eloRatingService.eloChange(k, winValue, winProbability);

        // Then
        assertEquals(-10, result);
    }

    @Test
    void EloChange_SuccessForHalfWinValue_ReturnZero() {
        // Given
        int k = 20;
        double winProbability = 0.5;
        double winValue = 0.5;

        // When
        double result = eloRatingService.eloChange(k, winValue, winProbability);

        // Then
        assertEquals(0, result);
    }

    @Test
    void EloCalculation_SuccessForValidInputs_Return380() throws IllegalArgumentException {
        // Given
        int elo1 = 400;
        int elo2 = 400;
        int outcome = 1;

        // When
        double result = eloRatingService.EloCalculation(elo1, elo2, outcome);

        // Then
        assertEquals(380, result);
    }

    @Test
    void EloCalculation_SuccessForValidInputWithDifferentK_Return2795() throws IllegalArgumentException {
        // Given
        int elo1 = 2800;
        int elo2 = 2800;
        int outcome = 1;

        // When
        double result = eloRatingService.EloCalculation(elo1, elo2, outcome);

        // Then
        assertEquals(2795, result);
    }
}
