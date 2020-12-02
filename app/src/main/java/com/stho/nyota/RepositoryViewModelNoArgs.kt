package com.stho.nyota

import android.app.Application
import com.stho.nyota.repository.Repository

abstract class RepositoryViewModelNoArgs(application: Application, repository: Repository) : AbstractViewModel(application, repository)