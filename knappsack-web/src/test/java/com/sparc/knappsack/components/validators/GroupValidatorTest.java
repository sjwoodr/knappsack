package com.sparc.knappsack.components.validators;

import com.sparc.knappsack.components.entities.Group;
import com.sparc.knappsack.components.entities.Organization;
import com.sparc.knappsack.components.entities.User;
import com.sparc.knappsack.components.services.GroupService;
import com.sparc.knappsack.components.services.UserService;
import com.sparc.knappsack.forms.GroupForm;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static junit.framework.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class GroupValidatorTest {
    @Mock
    private GroupService mockGroupService;

    @Mock
    private UserService mockUserService;

    @InjectMocks
    private GroupValidator validator = new GroupValidator();

    private Errors errors;
    private GroupForm groupForm;

    @Before
    public void setup() {
        groupForm = new GroupForm();
        errors = new BeanPropertyBindingResult(groupForm, "groupForm");
    }

    @Test
    public void testValidatorSupportsClass() {
        assertTrue(validator.supports(groupForm.getClass()));
    }

    @Test
    public void testValidatorNotSupportsClass() {
        assertFalse(validator.supports(String.class));
    }

    @Test
    public void testValid() {
        groupForm.setName("name");

        User user = new User();
        Organization organization = new Organization();
        organization.setId(1L);
        user.setActiveOrganization(organization);

        Mockito.when(mockUserService.getUserFromSecurityContext()).thenReturn(user);
        Mockito.when(mockGroupService.get(groupForm.getName(), user.getActiveOrganization().getId())).thenReturn(null);

        validator.validate(groupForm, errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    public void testEmptyName() {
        validator.validate(groupForm, errors);

        assertTrue(errors.hasErrors());
        Assert.assertEquals(errors.getErrorCount(), 1);
        assertNotNull(errors.getFieldError("name"));

        setup();

        groupForm.setName("");

        validator.validate(groupForm, errors);

        assertTrue(errors.hasErrors());
        Assert.assertEquals(errors.getErrorCount(), 1);
        assertNotNull(errors.getFieldError("name"));
    }

    @Test
    public void testGroupNameAlreadyExists() {
        groupForm.setName("name");

        User user = new User();
        Organization organization = new Organization();
        organization.setId(1L);
        user.setActiveOrganization(organization);

        Mockito.when(mockUserService.getUserFromSecurityContext()).thenReturn(user);
        Mockito.when(mockGroupService.get(groupForm.getName(), user.getActiveOrganization().getId())).thenReturn(new Group());

        validator.validate(groupForm, errors);

        assertTrue(errors.hasErrors());
        Assert.assertEquals(errors.getErrorCount(), 1);
        assertNotNull(errors.getFieldError("name"));
    }
}
