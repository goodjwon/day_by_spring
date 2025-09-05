#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
GitHub Issues to Markdown Export Script
사용법: python export_issues.py owner/repo-name [label-filter]
"""

import subprocess
import json
import sys
import os
from datetime import datetime
from collections import Counter

def run_gh_command(cmd):
    """GitHub CLI 명령 실행"""
    try:
        result = subprocess.run(cmd, shell=True, capture_output=True, text=True, encoding='utf-8')
        if result.returncode != 0:
            print(f"❌ 명령 실행 실패: {cmd}")
            print(f"에러: {result.stderr}")
            sys.exit(1)
        return result.stdout
    except Exception as e:
        print(f"❌ 명령 실행 중 오류: {e}")
        sys.exit(1)

def check_gh_cli():
    """GitHub CLI 설치 및 인증 확인"""
    try:
        subprocess.run(['gh', '--version'], capture_output=True, check=True)
    except (subprocess.CalledProcessError, FileNotFoundError):
        print("❌ GitHub CLI (gh)가 설치되어 있지 않습니다.")
        print("설치 방법: https://cli.github.com/")
        sys.exit(1)

    try:
        subprocess.run(['gh', 'auth', 'status'], capture_output=True, check=True)
    except subprocess.CalledProcessError:
        print("❌ GitHub CLI 인증이 필요합니다.")
        print("실행: gh auth login")
        sys.exit(1)

def get_issues(repo, label_filter=None):
    """이슈 목록 가져오기"""
    fields = "number,title,body,state,createdAt,updatedAt,author,labels,assignees,url"

    if label_filter:
        cmd = f'gh issue list --repo "{repo}" --label "{label_filter}" --state all --limit 100 --json {fields}'
    else:
        cmd = f'gh issue list --repo "{repo}" --state all --limit 100 --json {fields}'

    output = run_gh_command(cmd)
    return json.loads(output)

def create_markdown_file(repo, issues, label_filter, output_file):
    """마크다운 파일 생성"""

    with open(output_file, 'w', encoding='utf-8') as f:
        # 헤더 작성
        f.write("# GitHub Issues Export\n\n")
        f.write(f"**Repository:** {repo}  \n")
        f.write(f"**Export Date:** {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}  \n")
        f.write(f"**Filter:** {label_filter or 'All issues'}  \n\n")
        f.write("---\n\n")

        # 통계 정보
        f.write("## 📊 이슈 통계\n\n")
        f.write(f"- **총 이슈 수:** {len(issues)}\n")

        # 상태별 통계
        open_count = len([i for i in issues if i['state'] == 'open'])
        closed_count = len([i for i in issues if i['state'] == 'closed'])
        f.write(f"- **열린 이슈:** {open_count}\n")
        f.write(f"- **닫힌 이슈:** {closed_count}\n\n")

        # 라벨별 통계
        all_labels = []
        for issue in issues:
            for label in issue.get('labels', []):
                if label and 'name' in label:
                    all_labels.append(label['name'])

        if all_labels:
            f.write("### 🏷️ 주요 라벨\n\n")
            label_counts = Counter(all_labels)
            for label, count in label_counts.most_common(10):
                f.write(f"- **{label}:** {count}개\n")
            f.write("\n")

        f.write("---\n\n")

        # 각 이슈 상세 정보
        for issue in issues:
            number = issue['number']
            title = issue['title'] or '제목 없음'
            body = issue['body'] or '설명이 없습니다.'
            state = issue['state']
            created_at = issue['createdAt'][:10]
            updated_at = issue['updatedAt'][:10]
            author = issue['author']['login'] if issue['author'] else '알 수 없음'
            url = issue['url']

            # 라벨 정보
            labels = []
            for label in issue.get('labels', []):
                if label and 'name' in label:
                    labels.append(label['name'])

            # 담당자 정보
            assignees = []
            for assignee in issue.get('assignees', []):
                if assignee and 'login' in assignee:
                    assignees.append(assignee['login'])

            # 상태 이모지
            state_emoji = "🔓" if state == "open" else "🔒"

            # 이슈 정보 작성
            f.write(f"## {state_emoji} #{number} {title}\n\n")
            f.write(f"**📊 상태:** {state}  \n")
            f.write(f"**👤 작성자:** {author}  \n")
            f.write(f"**📅 생성일:** {created_at}  \n")
            f.write(f"**🔄 수정일:** {updated_at}  \n")

            if labels:
                labels_str = '` `'.join(labels)
                f.write(f"**🏷️ 라벨:** `{labels_str}`  \n")

            if assignees:
                f.write(f"**👥 담당자:** {', '.join(assignees)}  \n")

            f.write(f"**🔗 링크:** {url}\n\n")
            f.write("### 📝 설명\n\n")

            if body and body.strip():
                f.write(f"{body}\n\n")
            else:
                f.write("_설명이 없습니다._\n\n")

            f.write("---\n\n")

def main():
    if len(sys.argv) < 2:
        print("❌ 사용법: python export_issues.py owner/repo-name [label-filter]")
        print("예시: python export_issues.py goodjwon/day_by_spring")
        print("예시: python export_issues.py goodjwon/day_by_spring domain:book")
        sys.exit(1)

    repo = sys.argv[1]
    label_filter = sys.argv[2] if len(sys.argv) > 2 else None

    # 출력 파일명 설정
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    if label_filter:
        safe_label = label_filter.replace(':', '_').replace('/', '_')
        output_file = f"{safe_label}_issues_{timestamp}.md"
    else:
        output_file = f"all_issues_{timestamp}.md"

    print("🔍 GitHub 이슈를 가져오는 중...")
    print(f"📁 레포지토리: {repo}")
    print(f"🏷️  라벨 필터: {label_filter or '모든 라벨'}")
    print(f"📄 출력 파일: {output_file}")
    print()

    # GitHub CLI 확인
    check_gh_cli()

    # 이슈 가져오기
    issues = get_issues(repo, label_filter)

    print(f"📊 총 {len(issues)}개의 이슈를 찾았습니다.")

    if not issues:
        print("⚠️  조건에 맞는 이슈가 없습니다.")
        return

    # 마크다운 파일 생성
    print("📝 이슈들을 마크다운으로 변환 중...")
    create_markdown_file(repo, issues, label_filter, output_file)

    print()
    print(f"🎉 완료! 이슈들이 '{output_file}' 파일로 저장되었습니다.")
    print()
    print("📋 다음 명령어로 파일을 확인할 수 있습니다:")
    print(f"   cat '{output_file}'")
    print(f"   code '{output_file}'")
    print(f"   open '{output_file}'")

if __name__ == "__main__":
    main()