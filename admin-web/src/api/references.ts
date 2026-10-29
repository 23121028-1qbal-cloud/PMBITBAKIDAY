import { apiClient } from './client';
import { AdmissionWave, ApiResponse, StudyProgram } from '../types';

export async function fetchAdmissionWaves(): Promise<AdmissionWave[]> {
  const response = await apiClient.get<ApiResponse<AdmissionWave[]>>('/admission-waves');
  return response.data.data;
}

export async function fetchStudyPrograms(): Promise<StudyProgram[]> {
  const response = await apiClient.get<ApiResponse<StudyProgram[]>>('/study-programs');
  return response.data.data;
}
