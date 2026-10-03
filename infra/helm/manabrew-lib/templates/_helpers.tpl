{{/*
Common names and labels. Service charts call these instead of defining their own.
*/}}

{{- define "manabrew-lib.fullname" -}}
{{- .Values.nameOverride | default .Chart.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "manabrew-lib.selectorLabels" -}}
app.kubernetes.io/name: {{ include "manabrew-lib.fullname" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end -}}

{{- define "manabrew-lib.labels" -}}
{{ include "manabrew-lib.selectorLabels" . }}
app.kubernetes.io/version: {{ .Values.image.tag | default .Chart.AppVersion | quote }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app.kubernetes.io/part-of: manabrew
{{- end -}}
