# AnalyticsOps

Certainly, **testing** lies at the core of AnalyticsOps and is pivotal for achieving agility. Once implemented,
it enables the automated validation of dashboards, reports, and other Business Intelligence (BI) content
for accuracy, user experience, security/authorization, performance, and regressions. This results in fewer
errors and fosters greater trust in the analytics infrastructure.

[icCube](https://www.iccube.com) is releasing this project on GitHub with the following goals in mind:

- permissive license to reuse and extend at will.
- flexible enough to integrate into existing CI/CD pipeline.
- automated icCube server tests.
- automated data model tests.
- automated data authorization tests.
- automated dashboards tests.

Here is the link to the introductory post on
Medium: [AnalyticsOps and Automated Dashboard Tests](https://medium.com/@marc.polizzi/analyticsops-and-automated-dashboard-tests-535e2ab83ead).

## Roadmap

The project is still in its **early days** so stay tuned for further updates.
Here is a non-exhaustive [list](./ROADMAP.md) of upcoming features and developments

## Documentation

Refer to this [page](./etc/doc/en/README.md) for a technical description of the project or simply
browse the code to see how it works starting from the [shell](./src/main/java/ic3/analyticsops/shell/AOShell.java)
class.

## Release Notes

The release notes are available [here](./RELEASE_NOTES.md).

## Dashboard testing

To facilitate internal testing of dashboards in icCube, we provide a publicly available test suite: [ic3-dashboard-testing](https://github.com/ic3-software/ic3-dashboard-testing). This suite is implemented using Cypress.
