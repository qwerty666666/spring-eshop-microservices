# Returns absolute path to the script from which this method is called
function current_script_dir() {
  dirname "$(readlink -f "$0")"
}

# Returns absolute path to app root directory
function app_root_path() {
  dirname "$(current_script_dir)"
}

# Runs mvnw from application
function mvnw() {
  mvnw=$(app_root_path)/mvnw
  "$mvnw" "$@"
}