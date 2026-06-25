import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.PowerShellStep
import jetbrains.buildServer.configs.kotlin.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2026.1"

project {
    description = "Simple TeamCity CI pipeline for GitHub Flask application build and test validation."

    vcsRoot(HttpsGithubComVikasWanchooDevopsFlaskAPIDockerCicdRefsHeadsDevelop2)
    vcsRoot(HttpsGithubComVikasWanchooDevopsFlaskAPIDockerCicdRefsHeadsDevelop)
    vcsRoot(HttpsGithubComVikasWanchooDevopsFlaskAPIDockerCicdRefsHeadsDevelop1)

    buildType(FlaskAPIDockerCicd)
    buildType(Flast2)
    buildType(WindowsAgentBuild)
}

object FlaskAPIDockerCicd : BuildType({
    name = "FlaskAPI Docker CICD"

    artifactRules = """
        +:requirements.txt => artifacts
        +:Dockerfile => artifacts
        +:app/** => artifacts/app
    """.trimIndent()

    params {
        param("env.SECRET_KEY", "secret_666")
        param("API_KEY", "6727")
    }

    vcs {
        root(HttpsGithubComVikasWanchooDevopsFlaskAPIDockerCicdRefsHeadsDevelop)
    }

    steps {
        script {
            name = "Install dependencies and test Flask app"
            id = "Install_dependencies_and_test_Flask_app"
            enabled = false
            scriptContent = """
                set -eu
                
                echo "Current workspace:"
                pwd
                ls -la
                
                echo "Python version:"
                python3 --version
                
                echo "Creating clean virtual environment..."
                rm -rf .venv
                python3 -m venv .venv
                
                echo "Installing dependencies..."
                .venv/bin/python -m pip install --upgrade pip
                
                # Install project dependencies
                .venv/bin/python -m pip install -r requirements.txt
                
                # Fix Flask 2.2.x compatibility with Werkzeug
                .venv/bin/python -m pip install "Werkzeug==2.2.3"
                
                echo "Installed package versions:"
                .venv/bin/python - <<'PY'
                import flask
                import werkzeug
                
                print("Flask:", flask.__version__)
                print("Werkzeug:", werkzeug.__version__)
                PY
                
                echo "Validating Flask application..."
                .venv/bin/python - <<'PY'
                from app.app import app
                
                client = app.test_client()
                response = client.get("/")
                
                print("GET / status:", response.status_code)
                
                assert response.status_code == 200, f"Expected 200 but got {response.status_code}"
                
                print("Flask API route validation passed successfully")
                PY
            """.trimIndent()
        }
        powerShell {
            name = "variable test"
            id = "variable_test"
            scriptMode = script {
                content = """
                    # Plain parameter substitution
                    ${'$'}apiKey = "%API_KEY%"
                    Write-Host "Plain parameter value: ${'$'}apiKey"
                    
                    # Environment variable
                    ${'$'}secretKey = ${'$'}env:SECRET_KEY
                    Write-Host "Environment variable value: ${'$'}secretKey"
                """.trimIndent()
            }
            scriptExecMode = PowerShellStep.ExecutionMode.STDIN
        }
    }

    triggers {
        vcs {
            branchFilter = "+:<default>"
        }
    }

    dependencies {
        dependency(Flast2) {
            snapshot {
            }

            artifacts {
                buildRule = sameChain()
                artifactRules = "out.txt"
            }
        }
    }
})

object Flast2 : BuildType({
    name = "Flast2"

    artifactRules = "out.txt"

    vcs {
        root(HttpsGithubComVikasWanchooDevopsFlaskAPIDockerCicdRefsHeadsDevelop1)
    }

    steps {
        script {
            name = "build"
            id = "build"
            scriptContent = """echo "This file was compiled in Flast2" > out.txt"""
        }
    }

    triggers {
        vcs {
            branchFilter = "+:<default>"
        }
    }
})

object WindowsAgentBuild : BuildType({
    name = "WindowsAgentBuild"

    vcs {
        root(HttpsGithubComVikasWanchooDevopsFlaskAPIDockerCicdRefsHeadsDevelop2)
    }

    steps {
        script {
            name = "Windows Agent Test"
            id = "Windows_Agent_Test"
            scriptContent = """
                echo Running on Windows self-hosted TeamCity agent
                hostname
                echo Agent name: %teamcity.agent.name%
                python --version
                pip --version
            """.trimIndent()
        }
    }

    requirements {
        equals("teamcity.agent.name", "ip_223.181.113.205")
    }
})

object HttpsGithubComVikasWanchooDevopsFlaskAPIDockerCicdRefsHeadsDevelop : GitVcsRoot({
    name = "https://github.com/vikas-wanchoo-devops/FlaskAPI-Docker-CICD#refs/heads/develop"
    url = "https://github.com/vikas-wanchoo-devops/FlaskAPI-Docker-CICD"
    branch = "refs/heads/develop"
    branchSpec = "refs/heads/*"
    authMethod = password {
        userName = "vikas-wanchoo-devops"
        password = "cks0945d67f17e2216eb2eeb24280893a69/Hd9XNlC0AC7SC6CeITFjcwYYuUYZAjBbeXgM67EjQd7EWXmFFZuM2E1VO+urH5q"
    }
})

object HttpsGithubComVikasWanchooDevopsFlaskAPIDockerCicdRefsHeadsDevelop1 : GitVcsRoot({
    name = "https://github.com/vikas-wanchoo-devops/FlaskAPI-Docker-CICD#refs/heads/develop (1)"
    url = "https://github.com/vikas-wanchoo-devops/FlaskAPI-Docker-CICD"
    branch = "refs/heads/develop"
    branchSpec = "refs/heads/*"
    authMethod = password {
        userName = "vikas-wanchoo-devops"
        password = "cks0945d67f17e2216eb2eeb24280893a69/Hd9XNlC0AC7SC6CeITFjcwYYuUYZAjBbeXgM67EjQd7EWXmFFZuM2E1VO+urH5q"
    }
})

object HttpsGithubComVikasWanchooDevopsFlaskAPIDockerCicdRefsHeadsDevelop2 : GitVcsRoot({
    name = "https://github.com/vikas-wanchoo-devops/FlaskAPI-Docker-CICD#refs/heads/develop (2)"
    url = "https://github.com/vikas-wanchoo-devops/FlaskAPI-Docker-CICD"
    branch = "refs/heads/develop"
    branchSpec = "refs/heads/*"
    authMethod = password {
        userName = "vikas-wanchoo-devops"
        password = "cks0945d67f17e2216eb2eeb24280893a69/Hd9XNlC0AC7SC6CeITFjcwYYuUYZAjBbeXgM67EjQd7EWXmFFZuM2E1VO+urH5q"
    }
})
