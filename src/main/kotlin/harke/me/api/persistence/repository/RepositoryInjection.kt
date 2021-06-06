package harke.me.api.persistence.repository

import org.koin.dsl.module

object RepositoryInjection {
    val koinBeans = module {
        single<CvRepository> { CvRepositoryImpl() }
        single<WelcomeRepository> { WelcomeRepositoryImpl() }
    }
}