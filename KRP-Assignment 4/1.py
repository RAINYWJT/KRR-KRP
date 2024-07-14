def match_concepts_forward(concept_1, concept_2):
    first_concept_str = concept_1[0][1]
    if 'exists' in first_concept_str:
        first_letter = first_concept_str.split('.')[1][0]
        second_letter = concept_2[0][0]
        if first_letter == second_letter:
            return True
    return False

def match_concepts_back(concept_1, concept_2):
    first_concept_str = concept_2[0][0]
    second_letter = concept_1[0][1]
    if 'exists' in first_concept_str:
        first_letter = first_concept_str.split('.')[1][0]
        if first_letter == second_letter:
            return True
    return False

concept_1 = [('H', 'exists r.I'),0]
concept_2 =  [('I', 'A'),0]

concept_3 =  [('I', 'A'),0]
concept_4 = [('exists r.A', 'I'),0]

print(match_concepts_forward(concept_1, concept_2))  # 输出: True
print(match_concepts_back(concept_3,concept_4))