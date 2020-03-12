package introtask.resource;

import introtask.auth.UserAuth;
import introtask.dto.UserTagsDto;
import introtask.entity.Tag;
import introtask.logic.TagLogic;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TagResourceTest {


    @Mock
    private TagLogic tagLogic;

    @InjectMocks
    private TagResource testObject;

    @Before
    public void init() {
        testObject = new TagResource(tagLogic);
    }
    
    @Test
    public void createIfOk(){
        Tag tag1 = new Tag("AAAA","AAAA");
        Tag tag2 = new Tag("BBBB","BBBB");
        List<Tag> list = new ArrayList<Tag>();
        list.add(tag1);
        list.add(tag2);
        UserTagsDto tag = new UserTagsDto(UUID.randomUUID(),list);
        testObject.create(tag);
        verify(tagLogic).create(any());
    }

    @Test
    public void findIfOk(){
        UUID id = UUID.randomUUID();
        Tag tag1 = new Tag("AAAA","AAAA");
        Tag tag2 = new Tag("BBBB","BBBB");
        List<Tag> list = new ArrayList<Tag>();
        list.add(tag1);
        list.add(tag2);
        UserTagsDto tag = new UserTagsDto(id,list);
        given(tagLogic.getTagsByClientId(any(),any(),eq(0),eq(0))).willReturn(list);
        testObject.create(tag);
        //given(tagLogic.create( any() )).willReturn(list);
        Response res = testObject.getTagsByClientId(new UserAuth("1","2"), id.toString(), "AAAA", "AAAA", 0, 0);
        List<Tag> list2 = (List<Tag>)res.getEntity();
       // Assert.assertEquals(list, list2 );

       // List<Tag> list = new ArrayList<Tag>();
        //given(tagLogic.getTagsByClientId(any(), any(), any(), any())).willReturn(list);
        //testObject.getTagsByClientId(new UserAuth("1","2"), UUID.randomUUID().toString(), "AAAA", "AAAA", 0, 0);
        verify(tagLogic).getTagsByClientId(any(),any(Tag.class), eq(0), eq(0));
    }

    @Test
    public void doNotFindIfEmpty(){
        Response res = testObject.getTagsByClientId(new UserAuth("1","2"), "3", "4", "5", 0, 0);
       List<Tag> list = (List<Tag>)res.getEntity();
        Assert.assertEquals(list.size(),0 );
    }

    
}
