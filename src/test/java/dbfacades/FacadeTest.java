package dbfacades;

import dbfacades.DemoFacade;
import entity.Car;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * UNIT TEST example that mocks away the database with an in-memory database See
 * Persistence unit in persistence.xml in the test packages
 *
 * Use this in your own project by: - Delete everything inside the setUp method,
 * but first, observe how test data is created - Delete the single test method,
 * and replace with your own tests
 *
 */
public class FacadeTest {

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("pu-test", null);
//EntityManagerFactory emf = Persistence.createEntityManagerFactory("pu", null);

    DemoFacade facade = new DemoFacade(emf);

    /**
     * Setup test data in the database to a known state BEFORE Each test
     */
    @Before
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            //Delete all, since some future test cases might add/change data
            em.createQuery("delete from Car").executeUpdate();
            //Add our test data
            Car e1 = new Car("Volve");
            Car e2 = new Car("WW");
            em.persist(e1);
            em.persist(e2);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    // Test the single method in the Facade
    @Test
    public void countEntities() {
        EntityManager em = emf.createEntityManager();
        try {
            long count = facade.countCars();
            Assert.assertEquals(2, count);
        } finally {
            em.close();
        }
    }

    @Test
    public void testGetAllCars() {
        List<Car> allCars = facade.getAllCars();

        Assert.assertEquals(2, allCars.size());
    }

    @Test
    public void testGetCarsByMake() {
        final String MAKE = "Volve";
        List<Car> carsByMake = facade.getCarsByMake(MAKE);

        Assert.assertEquals(1, carsByMake.size());
        Assert.assertEquals(MAKE, carsByMake.get(0).getMake());
    }

    @Test
    public void testGetCarById() {
        // not independent from another method which might fail
        // means that when this test fails u wont know the reason for it.
        List<Car> allCars = facade.getAllCars();
        Car car = allCars.get(0);
        int idOfCar = car.getId();
        
        Car car2 = facade.getCarById(idOfCar+1);
        
        Assert.assertEquals(car2.getId(), allCars.get(1).getId());
        
    }
    
    @Test 
    public void testDeleteCarById() {
        List<Car> cars = facade.getAllCars();
        int carId = cars.get(0).getId();
        
        Car car = facade.deleteCarById(carId);
        //Car car = cars.get(0);
        //List<Car> carsAfterDeletion = facade.getAllCars();
        
        List<Car> carsAfterDeletion = facade.getAllCars();
        
        Assert.assertNotEquals(cars.get(0), car);
        Assert.assertEquals("Volve", cars.get(0).getMake());
        Assert.assertEquals("WW", carsAfterDeletion.get(0).getMake());
        
        
    }

}
