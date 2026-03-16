# Git Workflow Reflection

## Git Challenges

During the development of the Smart Campus Companion project, two
feature branches were used: **feature/announcements** and
**feature/task-manager**. This allowed different features to be
developed simultaneously, but it also created challenges when
integrating the changes into the **develop** branch.

Because both branches modified parts of the same project structure,
especially UI and screen-related files, Git was sometimes unable to
automatically merge the branches. This resulted in merge conflicts that
required manual resolution. The experience showed the importance of
frequently syncing feature branches with the **develop** branch to
minimize conflicts.

## Conflict Resolution

When attempting to merge **feature/announcements** into **develop**,
GitHub detected a conflict in:

`app/src/main/java/com/example/smartcampuscompanion/screens/TaskManagerScreen.kt`

Although the merge involved the announcements feature, the conflict
occurred because both branches modified related UI components and
imports in the project. Git could not automatically decide which version
of the code to keep.

The conflict was resolved using GitHub's conflict editor by: 1.
Reviewing the conflicting code sections. 2. Comparing the **current
changes** with the **incoming changes**. 3. Choosing the correct code
using *Accept current*, *Accept incoming*, or *Accept both*. 4. Removing
conflict markers and committing the resolved file.

After resolving the conflict, GitHub confirmed that there were **no
conflicts with the base branch**, allowing the pull request to be merged
successfully.

