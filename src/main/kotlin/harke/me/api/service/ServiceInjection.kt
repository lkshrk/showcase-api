package harke.me.api.service

import org.koin.dsl.module

object ServiceInjection {
    val koinBeans = module {
        single<CvService> { CvServiceImpl() }
        single<WelcomeService> { WelcomeServiceImpl() }
    }
}