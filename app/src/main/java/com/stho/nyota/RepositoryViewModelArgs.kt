package com.stho.nyota

import android.app.Application
import com.stho.nyota.repository.Repository

abstract class RepositoryViewModelArgs(application: Application, repository: Repository) : AbstractViewModel(application, repository)