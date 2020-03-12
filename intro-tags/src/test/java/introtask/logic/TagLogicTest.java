package introtask.logic;
import introtask.dao.TagDao;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;


@RunWith(MockitoJUnitRunner.class)
public class TagLogicTest {

    @Mock
    private TagDao tagDao;

    private TagLogic testObject;

    @Before
    public void init() {
        testObject = new TagLogic(tagDao);
    }




}
