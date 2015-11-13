package org.jfl2.file;

import org.junit.Test;

import java.nio.file.Paths;
import java.nio.file.attribute.UserPrincipal;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class Jfl2PathTest {

    @Test
    public void testGetSize() throws Exception {
        Jfl2Path path = new Jfl2Path(Paths.get("build/resources/test/.jfl2/groovy/dummy.txt"));
        assertThat(path.getSize(), is(new LongHasString(28L)));
    }

    @Test
    public void testGetOwner() throws Exception {
        Jfl2Path path = new Jfl2Path(Paths.get("build/resources/test/.jfl2/groovy/main.groovy"));
        Optional<UserPrincipal> opt = path.getOwner();
        assertThat(opt, notNullValue());
        assertThat(opt.isPresent(), is(true));
        opt.ifPresent(owner -> {
            assertThat(owner.getName(), not(""));
        });
    }

}
