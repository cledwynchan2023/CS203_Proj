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
    private EloRatingServiceImplementation EloRatingService;

    @Test
    void isValidElo_ValidElo_returnTrue() {
        int elo = 1000;

        boolean result = EloRatingService.isValidElo(elo);

        assertTrue(result);
    }

    @Test
    void isValidElo_InValidEloNeg_returnFalse() {
        int elo = -1;

        boolean result = EloRatingService.isValidElo(elo);

        assertEquals(false, result);
    }

    @Test
    void getKValue_KBetween_1000AND2731UPPERPORTION() {
        int elo = 2731;

        int result = EloRatingService.getKValue(elo);

        assertEquals(11, result);
    }

    @Test
    void getKValue_KBetween_515AND2731LOWERPORTION() {
        int elo = 515;

        int result = EloRatingService.getKValue(elo);

        assertEquals(39, result);
    }

    @Test
    void getKValue_kLessThan515_returns40() {
        int elo = 514;

        int result = EloRatingService.getKValue(elo);

        assertEquals(40, result);
    }

    @Test
    void getKValue_Greater_Than2731() {
        int elo = 2732;

        int result = EloRatingService.getKValue(elo);

        assertEquals(10, result);
    }

    @Test
    void WinProbabilityOnElo_validInputs_returnDouble() {
        int elo1 = 1000;
        int elo2 = 1000;

        double result = EloRatingService.WinProbabilityOnElo(elo1, elo2);

        assertEquals(0.5, result);
    }

    @Test
    void WinValue_outcome1_return1() {
        int outcome = -1;

        double result = EloRatingService.WinValue(outcome);

        assertEquals(1, result);
    }

    @Test
    void WinValue_outcome2_return0() {
        int outcome = 1;

        double result = EloRatingService.WinValue(outcome);

        assertEquals(0, result);
    }

    @Test
    void WinValue_outcome3_returnhalf() {
        int outcome = 0;

        double result = EloRatingService.WinValue(outcome);

        assertEquals(0.5, result);
    }

    @Test
    void WinValue_noOutcome_returnneg1() {
        int outcome = 123;

        double result = EloRatingService.WinValue(outcome);

        assertEquals(-1, result);
    }

    @Test
    void EloCalculation_Success_FloorAt0() {
        int elo1 = 5;
        int elo2 = 5;
        int outcome = 1;

        double result=EloRatingService.EloCalculation(elo1, elo2, outcome);
        
        assertEquals(0.0,result);
    }

    @Test
    void EloCalculation_InvalidElo1_ReturnIllegalArgumentException() {
        int elo1 = -1;
        int elo2 = 1000;
        int outcome = 1;
        boolean exceptionThrown = false;

        try {

            EloRatingService.EloCalculation(elo1, elo2, outcome);

        } catch (IllegalArgumentException e) {

            assertEquals("Invalid elo values", e.getMessage());
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
    }

    @Test
    void EloCalculation_InvalidElo2_ReturnIllegalArgumentException() {
        int elo1 = 1000;
        int elo2 = -1;
        int outcome = 1;
        boolean exceptionThrown = false;

        try {

            EloRatingService.EloCalculation(elo1, elo2, outcome);

        } catch (IllegalArgumentException e) {

            assertEquals("Invalid elo values", e.getMessage());
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
    }

    @Test
    void EloCalculation_InvalidOutcome_ReturnIllegalArgumentException() {
        int elo1 = 1000;
        int elo2 = 1000;
        int outcome = 12;
        boolean exceptionThrown = false;

        try {

            EloRatingService.EloCalculation(elo1, elo2, outcome);

        } catch (IllegalArgumentException e) {

            assertEquals("Invalid match result only accepts -1 for A wins, 1 for B wins and 0 for draw",
                    e.getMessage());

            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
    }

    @Test
    void EloCalculationForBoth_Success() throws IllegalArgumentException {
        int elo1 = 2800;
        int elo2 = 2800;
        int outcome = 1;

        List<Double> result = EloRatingService.eloRatingForBoth(elo1, elo2, outcome);
        assertEquals(2795, result.get(0));
        assertEquals(2805, result.get(1));
    }

    @Test
    void EloChange_ReturnDouble() {
        int k = 20;
        double winProbability = 0.5;
        double winValue = 1;

        double result = EloRatingService.eloChange(k, winValue, winProbability);

        assertEquals(10, result);
    }

    @Test
    void EloChange_WV0_ReturnDouble() {
        int k = 20;
        double winProbability = 0.5;
        double winValue = 0;

        double result = EloRatingService.eloChange(k, winValue, winProbability);

        assertEquals(-10, result);
    }

    @Test
    void EloChange_WV05_ReturnDouble() {
        int k = 20;
        double winProbability = 0.5;
        double winValue = 0.5;

        double result = EloRatingService.eloChange(k, winValue, winProbability);

        assertEquals(0, result);
    }

    @Test
    void EloCalculation_ValidInputs_ReturnDouble() throws IllegalArgumentException {
        int elo1 = 400;
        int elo2 = 400;
        int outcome = 1;

        double result = EloRatingService.EloCalculation(elo1, elo2, outcome);
        assertEquals(380, result);
    }

    @Test
    void EloCalculation_ValidInputDiffK_ReturnDouble() throws IllegalArgumentException {
        int elo1 = 2800;
        int elo2 = 2800;
        int outcome = 1;

        double result = EloRatingService.EloCalculation(elo1, elo2, outcome);
        assertEquals(2795, result);
    }
}
