package com.hlt.jzwebsite.viewmodel

import com.hlt.jzwebsite.base.paging.BasePagingViewModel
import com.hlt.jzwebsite.model.Project
import com.hlt.jzwebsite.paging.repository.ProjectRepository

/**
 * @author LXB
 * @description:
 * @date :2020/3/5 11:20
 */
class ProjectViewModel(repository: ProjectRepository) : BasePagingViewModel<Project>(repository) {

}
