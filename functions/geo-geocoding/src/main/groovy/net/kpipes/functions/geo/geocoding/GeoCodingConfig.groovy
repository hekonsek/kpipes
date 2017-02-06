package net.kpipes.functions.geo.geocoding

import net.kpipes.core.function.FunctionBinding
import net.kpipes.core.starter.KPipes
import net.kpipes.core.starter.Service
import org.apache.camel.ProducerTemplate
import org.apache.camel.impl.DefaultCamelContext

class GeoCodingConfig {

    @Service
    FunctionBinding coreFilterFunctionBinding(KPipes kpipes) {
        def camelProducerTenmplate = kpipes.service(ProducerTemplate)
        new FunctionBinding(kpipes, 'geo.coding.reverse', new GeoCodingReverseFunction(camelProducerTenmplate)).start()
    }

    @Service
    ProducerTemplate camelProducerTemplate(KPipes kpipes) {
        def camel = new DefaultCamelContext()
        camel.start()
        camel.createProducerTemplate()
    }

}