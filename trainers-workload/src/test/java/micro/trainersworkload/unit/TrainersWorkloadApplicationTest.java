package micro.trainersworkload.unit;

import micro.trainersworkload.TrainersWorkloadApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TrainersWorkloadApplicationTest {

      @Test
      void contextLoads() {
      }

      @Test
      void applicationStartsSuccessfully() {
            TrainersWorkloadApplication.main(new String[] {});
      }
}
