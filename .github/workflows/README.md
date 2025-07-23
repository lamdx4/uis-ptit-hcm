# GitHub Actions CI/CD Pipeline

This project uses GitHub Actions for continuous integration and deployment. The pipeline includes multiple workflows to ensure code quality, automated testing, and reliable releases.

## Workflows

### 1. 🚀 Android CI (`android-ci.yml`)
**Triggers:** Push to `main`/`develop`, Pull Requests to `main`/`develop`

**Jobs:**
- **Test**: Run unit tests with test report uploads on failure
- **Build**: Build both debug and release APKs with artifact uploads
- **Lint**: Run Android lint checks with report uploads

**Features:**
- Gradle caching for faster builds
- Parallel job execution
- Artifact uploads for debugging

### 2. 🔍 Code Quality (`code-quality.yml`)
**Triggers:** Push to `main`/`develop`, Pull Requests to `main`/`develop`

**Jobs:**
- **Kotlin Lint**: Run ktlint for code style consistency
- **Security Scan**: Check dependencies for vulnerabilities
- **PR Checks**: Validate PR titles, commit messages, and file sizes

**Validation Rules:**
- PR titles must follow conventional format: `type(scope): description`
- Commit messages must follow conventional format
- Files must be < 10MB
- Supported types: `feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `chore`

### 3. 📦 Release (`release.yml`)
**Triggers:** Push tags starting with `v*` (e.g., `v1.0.0`)

**Jobs:**
- **Build Release**: Build signed release APK
- **Create Release**: Automatically create GitHub release with changelog
- **Notify**: Send build status notifications

**Features:**
- Automatic version extraction from git tags
- Release notes generation
- APK upload to GitHub releases

### 4. 📊 Performance Monitoring (`performance.yml`)
**Triggers:** Push to `main`, Pull Requests to `main`

**Jobs:**
- **Build Benchmark**: Measure build times and APK sizes
- **Memory Leak Check**: Run memory profiling during builds

**Metrics:**
- Build time monitoring (warns if > 5 minutes)
- APK size validation (fails if > 50MB)
- Memory usage tracking
- Performance comments on PRs

## Setup Instructions

### Prerequisites
1. Enable GitHub Actions in your repository
2. Ensure `gradlew` has execute permissions
3. Set up any required secrets (e.g., signing keys)

### Required Permissions
- Actions: Read/Write (for workflow runs)
- Contents: Write (for creating releases)
- Issues: Write (for PR comments)
- Pull Requests: Write (for PR checks)

### Optional Secrets
- `GITHUB_TOKEN`: Automatically provided by GitHub
- `ANDROID_SIGNING_KEY`: Base64 encoded keystore (for release signing)
- `ANDROID_SIGNING_PASSWORD`: Keystore password

## Usage

### Running Tests Locally
```bash
./gradlew test
./gradlew lintDebug
```

### Creating a Release
1. Create and push a tag:
```bash
git tag v1.0.0
git push origin v1.0.0
```
2. The release workflow will automatically trigger
3. Check the "Releases" section for the built APK

### Manual Workflow Triggers
Some workflows can be triggered manually:
- Release: Create and push a tag to trigger release workflow
- Performance Monitoring: Triggered automatically on main branch

## Monitoring

### Build Status
- Check the "Actions" tab for workflow status
- Green checkmarks ✅ indicate passing builds
- Red X marks ❌ indicate failures

### Artifacts
Failed builds upload diagnostic artifacts:
- Test reports (HTML format)
- Lint reports (HTML format)
- Heap dumps (for memory issues)

### Performance Reports
PR builds include performance comments with:
- Build time measurements
- APK size validation
- Memory usage analysis

## Troubleshooting

### Common Issues

1. **Build Timeout**
   - Increase runner timeout in workflow
   - Check for infinite loops or hanging processes

2. **Gradle Cache Issues**
   - Clear cache by updating cache key
   - Check Gradle wrapper version compatibility

3. **Permission Denied on gradlew**
   - Ensure `chmod +x gradlew` step is present
   - Check if gradlew is committed with correct permissions

4. **Large APK Size**
   - Enable ProGuard/R8 shrinking
   - Remove unused resources
   - Optimize images and assets

### Getting Help
- Check workflow logs in the Actions tab
- Review error messages in job outputs
- Verify environment setup and dependencies

## Best Practices

1. **Keep workflows fast**: Use caching and parallel jobs
2. **Test before merge**: Ensure CI passes before merging PRs
3. **Follow conventions**: Use conventional commit messages and PR titles
4. **Monitor performance**: Review build times and APK sizes regularly
5. **Update dependencies**: Check weekly dependency update reports
