package employee;
import org.springframework.data.jpa.repository.JpaRepository;

// A servlet container is fired up and serves the service
interface EmployeeRepository extends JpaRepository<Employee, Long> {
}