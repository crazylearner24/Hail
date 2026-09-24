package com.hail.app
import android.app.Application
import com.hail.app.data.api.ApiClient
import com.hail.app.data.repository.*
import com.hail.app.session.SessionManager

class HailApplication : Application() {
    lateinit var session: SessionManager; lateinit var auth: AuthRepository; lateinit var tests: TestRepository
    override fun onCreate() {
        super.onCreate()
        session = SessionManager(this)
        val api = ApiClient.create(session)
        auth = AuthRepository(api, session); tests = TestRepository(api)
    }
}
