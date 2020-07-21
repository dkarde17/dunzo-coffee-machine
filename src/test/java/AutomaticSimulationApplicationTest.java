import com.dunzo.coffee_machine.CoffeeMachineSimulation;
import com.dunzo.coffee_machine.OutletResponse;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

public class AutomaticSimulationApplicationTest {

    /**
     * simple test to check that all the beverages are made
     * input has enough initial item quantities that both the beverages can be prepared
     */
    @Test
    public void allBeveragesAreMade() {
        List<OutletResponse> results = CoffeeMachineSimulation.run(new String[]{"input1.json"});

        //asserting that both the beverages were prepared
        Assert.assertEquals(2, results.stream().filter(x -> x.wasBeveragePrepared()).count());
        //asserting that there was no beverage that was not prepared
        Assert.assertEquals(0, results.stream().filter(x -> !x.wasBeveragePrepared()).count());
    }

    /**
     * one of the beverages should not be prepared due to the lack of the ingredients
     */
    @Test
    public void oneBeverageIsNotMadeBecauseOfInsufficientIngredients(){
        List<OutletResponse> results = CoffeeMachineSimulation.run(new String[]{"input2.json"});

        //asserting that two beverages were prepared
        Assert.assertEquals(2, results.stream().filter(x -> x.wasBeveragePrepared()).count());
        //asserting that one beverage was not prepared
        Assert.assertEquals(1, results.stream().filter(x -> !x.wasBeveragePrepared()).count());
    }

    /**
     * one of the beverages should not be prepared due to the lack of the ingredients
     * This one differs from the previous test case in the way that the ingredient is missing and not just the quantity
     */
    @Test
    public void oneBeverageIsNotMadeBecauseOfMissingIngredients(){
        List<OutletResponse> results = CoffeeMachineSimulation.run(new String[]{"input3.json"});

        //asserting that two beverages were prepared
        Assert.assertEquals(2, results.stream().filter(x -> x.wasBeveragePrepared()).count());
        //asserting that one beverage was not prepared
        List<OutletResponse> failedBeverages = results.stream().filter(x -> !x.wasBeveragePrepared()).collect(Collectors.toList());
        Assert.assertEquals(1, failedBeverages.size());
        Assert.assertEquals("green_tea", failedBeverages.get(0).getBeverageName());
    }

    /**
     * 4 beverages should be prepared because of the refill
     * we refill missing as well as insufficient ingredients
     */
    @Test
    public void allBeveragesArePreparedBecauseOfTheRefill(){
        List<OutletResponse> results = CoffeeMachineSimulation.run(new String[]{"input4.json"});

        //asserting that all 4 beverages were prepared as a result of the refill
        Assert.assertEquals(4, results.stream().filter(x -> x.wasBeveragePrepared()).count());
        //asserting that no beverages failed
        Assert.assertEquals(0, results.stream().filter(x -> !x.wasBeveragePrepared()).count());
    }
}