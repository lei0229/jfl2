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

    @Test
    public void testEquals() throws Exception {
        Jfl2Path path1 = new Jfl2Path(Paths.get("build/resources/test/.jfl2/groovy/main.groovy"));
        Jfl2Path path2 = new Jfl2Path(Paths.get("build/resources/test/.jfl2/groovy/main.groovy"));
        assertThat(path1.equals(path2), is(true));
    }

    @Test
    public void testEquals_virtualPath() throws Exception {
        Jfl2Path p1 = new Jfl2Path(Paths.get("parent"));
        Jfl2Path c1 = new Jfl2Path(p1, Paths.get("child"));
        Jfl2Path p2 = new Jfl2Path(Paths.get("parent"));
        Jfl2Path c2 = new Jfl2Path(p2, Paths.get("child"));
        assertThat(c1.equals(c2), is(true));
    }

    @Test
    public void testEquals_virtualPath2() throws Exception {
        // not equals のテスト
        Jfl2Path p1 = new Jfl2Path(Paths.get("parent"));
        Jfl2Path c1 = new Jfl2Path(p1, Paths.get("child"));
        Jfl2Path p2 = new Jfl2Path(Paths.get("parent2"));
        Jfl2Path c2 = new Jfl2Path(p2, Paths.get("child"));
        assertThat(c1.equals(c2), is(false));
    }

}
