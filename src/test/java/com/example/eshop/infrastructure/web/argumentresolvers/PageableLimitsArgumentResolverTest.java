package com.example.eshop.infrastructure.web.argumentresolvers;

import com.example.eshop.infrastructure.web.PageableSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class PageableLimitsArgumentResolverTest {
    private PageableWithSettingsArgumentResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new PageableWithSettingsArgumentResolver(Optional.empty(), Optional.empty());
    }

    @Test
    void shouldSupportPageableArgumentWithPageableSettings() throws NoSuchMethodException {
        var param = new MethodParameter(Sample.class.getMethod("supportedMethod", Pageable.class), 0);

        assertThat(resolver.supportsParameter(param)).isTrue();
    }

    @Test
    void shouldNotSupportPageableArgumentWithoutPageableSettings() throws NoSuchMethodException {
        var param = new MethodParameter(Sample.class.getMethod("simplePageableMethod", Pageable.class), 0);

        assertThat(resolver.supportsParameter(param)).isFalse();
    }

    @Test
    void shouldNotSupportNonPageableArgument() throws NoSuchMethodException {
        var param = new MethodParameter(Sample.class.getMethod("nonPageableMethod", String.class), 0);

        assertThat(resolver.supportsParameter(param)).isFalse();
    }

    @Test
    void shouldRejectNonPositiveMaxPageSize() throws NoSuchMethodException {
        var param = new MethodParameter(Sample.class.getMethod("invalidMaxPageSize", Pageable.class), 0);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> resolver.resolveArgument(param, null, createRequestMock(), null));
    }

    @Test
    void shouldUseMaxPageSizeWhenRequestedPageSizeExceededLimit() throws NoSuchMethodException {
        var request = new MockHttpServletRequest();
        request.addParameter("size", "200");
        var param = new MethodParameter(Sample.class.getMethod("supportedMethod", Pageable.class), 0);

        var pageable = resolver.resolveArgument(param, null, new ServletWebRequest(request), null);

        assertThat(pageable).isEqualTo(PageRequest.of(0, 100));
    }

    @Test
    void shouldUseDefaultPageSizeWhenNotSpecified() throws NoSuchMethodException {
        var request = new MockHttpServletRequest();
        var param = new MethodParameter(Sample.class.getMethod("supportedMethod", Pageable.class), 0);

        var pageable = resolver.resolveArgument(param, null, new ServletWebRequest(request), null);

        int defaultPageSize = (int) PageableSettings.class.getMethod("defaultPageSize").getDefaultValue();
        assertThat(pageable).isEqualTo(PageRequest.of(0, defaultPageSize));
    }

    private NativeWebRequest createRequestMock() {
        return new ServletWebRequest(new MockHttpServletRequest());
    }

    private interface Sample {
        void supportedMethod(@PageableSettings(maxPageSize = 100) Pageable pageable);
        void simplePageableMethod(Pageable pageable);
        void nonPageableMethod(@PageableSettings(maxPageSize = 100) String s);
        void invalidMaxPageSize(@PageableSettings(maxPageSize = 0) Pageable pageable);
    }
}