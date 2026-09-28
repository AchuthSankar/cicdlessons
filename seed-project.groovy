folder('ci') {
    displayName('CI')
    description('Folder for seed and generated Jenkins jobs')
}

pipelineJob('ci/seed-project') {
    description('Seed job that creates a project from user-provided Git parameters')

    parameters {
        stringParam('GIT_URL', 'https://github.com/your-org/your-repo.git', 'Git repository URL')
        stringParam('GIT_BRANCH', 'main', 'Git branch to build')
        stringParam('SCRIPT_PATH', 'Jenkinsfile', 'Path to Jenkinsfile inside the repo')
        stringParam('JOB_NAME', 'Job1', 'Job Folder Name')
    }

    definition {
        cps {
            sandbox()
            script('''
                pipeline {
                    agent any

                    stages {
                        stage('Create generated job') {
                            steps {
                                jobDsl(
                                    scriptText: '''
                                        folder('ci') {
                                            displayName('CI')
                                            description('Generated CI jobs')
                                        }

                                        pipelineJob("ci/${params.JOB_NAME}") {
                                            definition {
                                                cpsScm {
                                                    scm {
                                                        git {
                                                            remote {
                                                                url(params.GIT_URL)
                                                            }
                                                            branches(params.GIT_BRANCH)
                                                        }
                                                    }
                                                    scriptPath(params.SCRIPT_PATH)
                                                }
                                            }
                                        }
                                    '''.stripIndent(),
                                    removedJobAction: 'DELETE',
                                    removedViewAction: 'DELETE'
                                )
                            }
                        }
                    }
                }
            '''.stripIndent())
        }
    }
}
