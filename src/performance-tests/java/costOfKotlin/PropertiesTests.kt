package costOfKotlin

import com.natpryce.hamkrest.assertion.assertThat
import com.oneeyedmen.kostings.probablyDifferentTo
import com.oneeyedmen.kostings.probablyFasterThan
import costOfKotlin.properties.JavaProperties
import costOfKotlin.properties.KotlinProperties
import org.junit.Test

class PropertiesTests {

    @Test
    fun java() {
        assertThat(JavaProperties::field_access,
            ! probablyDifferentTo(JavaProperties::getter))
        assertThat(JavaProperties::method_access,
            probablyFasterThan(JavaProperties::field_access, byMoreThan = 0.0002, butNotMoreThan = 0.0005))
    }

    @Test
    fun kotlin() {
        assertThat(KotlinProperties::method_property, ! probablyDifferentTo(KotlinProperties::constant_method))
        assertThat(KotlinProperties::method_property, ! probablyDifferentTo(JavaProperties::method_access))

        assertThat(KotlinProperties::field_property, probablyFasterThan(JavaProperties::field_access, byMoreThan = 0.015, butNotMoreThan = 0.02))
        assertThat(KotlinProperties::field_property, probablyFasterThan(JavaProperties::getter, byMoreThan = 0.015, butNotMoreThan = 0.02))
        assertThat(KotlinProperties::field_property, probablyFasterThan(JavaProperties::method_access, byMoreThan = 0.015, butNotMoreThan = 0.02))
        assertThat(KotlinProperties::field_property, probablyFasterThan(KotlinProperties::method_property, byMoreThan = 0.015, butNotMoreThan = 0.02))
        assertThat(KotlinProperties::field_property, probablyFasterThan(KotlinProperties::constant_method, byMoreThan = 0.015, butNotMoreThan = 0.02))
    }

}