package com.beloushkin.android.learn.countries

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.beloushkin.android.learn.countries.model.CountriesService
import com.beloushkin.android.learn.countries.model.Country
import com.beloushkin.android.learn.countries.viewmodel.ListViewModel
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.Disposable
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.plugins.RxJavaPlugins
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

class ListViewModelTest {

    @get:Rule
    var rule = InstantTaskExecutorRule()

    // write tests!!!
    // mocks injecting
    @Mock
    lateinit var countriesService: CountriesService

    @InjectMocks
    var listViewModel = ListViewModel()

    // setup testing

    private var testSingle: Single<List<Country>>? = null

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    // tests - success path

    @Test
    fun getCountriesSuccess() {
        val country = Country("countryName", "capital", "url")
        val countriesList = arrayListOf(country, country)

        testSingle = Single.just(countriesList)

        `when`(countriesService.getCountries()).thenReturn(testSingle)

        listViewModel.refresh()

        Assert.assertEquals(2, listViewModel.countries.value?.size)
        Assert.assertEquals(false, listViewModel.countryLoadError.value)
        Assert.assertEquals(false, listViewModel.loading.value)
    }

    @Test
    fun getCountriesFail() {
        testSingle = Single.error(Throwable())

        `when`(countriesService.getCountries()).thenReturn(testSingle)
        listViewModel.refresh()

        Assert.assertEquals(true, listViewModel.countryLoadError.value)
        Assert.assertEquals(false, listViewModel.loading.value)
    }

    @Before
    fun setUpRxSchedulers() {
        val immediate = object: Scheduler() {

            override fun scheduleDirect(run: Runnable?, delay: Long, unit: TimeUnit?): Disposable {
                return super.scheduleDirect(run, 0, unit)
            }

            override fun createWorker(): Worker {
                return ExecutorScheduler.ExecutorWorker(Executor { it.run() })
            }
        }

        RxJavaPlugins.setInitIoSchedulerHandler { scheduler -> immediate }
        RxJavaPlugins.setInitComputationSchedulerHandler { scheduler -> immediate}
        RxJavaPlugins.setInitIoSchedulerHandler { scheduler -> immediate }
        RxJavaPlugins.setInitSingleSchedulerHandler { scheduler -> immediate }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler -> immediate }
    }
}