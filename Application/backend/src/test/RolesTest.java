import com.gleb.data.user.Roles;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RolesTest {

    @Test
    public void getRole() {
        String role = "ADMIN";
        Set<Roles> roleSet = new HashSet<>();
        roleSet.add(Roles.ADMIN);
        Roles[] roleArray = roleSet.toArray(new Roles[0]);
        assertEquals(Roles.ADMIN, roleArray[0]);
    }
}